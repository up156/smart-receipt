package com.smart_receipt.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart_receipt.config.GigachatProperties;
import com.smart_receipt.dto.CategoryDto;
import com.smart_receipt.dto.RecognitionResultDto;
import com.smart_receipt.dto.ai.AccessTokenDto;
import com.smart_receipt.dto.ai.AiReplyDto;
import com.smart_receipt.ex.AiResponseException;
import com.smart_receipt.feign.FeignAuthClient;
import com.smart_receipt.feign.FeignToGigachatClient;
import com.smart_receipt.request.GigachatMessage;
import com.smart_receipt.request.GigachatRequest;
import com.smart_receipt.service.RecognitionService;
import com.smart_receipt.util.PromptUtil;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.smart_receipt.util.PromptUtil.IMAGE_SYSTEM_CONTENT;
import static com.smart_receipt.util.PromptUtil.TEXT_SYSTEM_CONTENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecognitionServiceImpl implements RecognitionService {

    private final ITesseract tesseract;

    private final FeignAuthClient feignAuthClient;

    private final FeignToGigachatClient feignToGigachatClient;

    private final MeterRegistry meterRegistry;

    private final GigachatProperties properties;

    private final ObjectMapper objectMapper;

    private AccessTokenDto accessTokenDto;

    @Value("${gigachat.ai.token}")
    private String credentials;

    @Value("${gigachat.ai.scope}")
    private String scope;

    @Value("${feign.ai-auth.url}")
    private String authUrl;


    @Override
    public RecognitionResultDto processImage(List<CategoryDto> categories, MultipartFile file) {

        log.info("Recognition service started processing OCR Image recognition");
        String extractedText = extractTextFromImage(file);
        log.info("extractedText: {}", extractedText);
        GigachatRequest request = buildPrompt(extractedText, categories, IMAGE_SYSTEM_CONTENT);
        return recognizeWithFallback(request, categories);
    }

    @Override
    public RecognitionResultDto processText(List<CategoryDto> categories, String extractedText) {

        log.info("Recognition service started processing text recognition");
        GigachatRequest request = buildPrompt(extractedText, categories, TEXT_SYSTEM_CONTENT);
        return recognizeWithFallback(request, categories);
    }

    private String extractTextFromImage(MultipartFile image) {
        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            return tesseract.doOCR(bufferedImage);
        } catch (IOException | TesseractException e) {
            log.error("Failed to extract text from image", e);
            throw new IllegalStateException("Failed to extract text from image", e);
        }
    }

    private GigachatRequest buildPrompt(String rawText, List<CategoryDto> categories, String systemContent) {

        String categoryList = categories.stream()
                .map(c -> "- \"" + c.getName() + "\"")
                .collect(Collectors.joining("\n"));

        String userContent = """
                Категории: %s
                Текст чека:
                %s
                """
                .formatted(categoryList, rawText);

        return GigachatRequest.builder()
                .model(properties.getModel())
                .temperature(properties.getTemperature())
                .variantsCount(properties.getN())
                .maxTokens(properties.getMaxTokens())
                .repetitionPenalty(properties.getRepetitionPenalty())
                .stream(false)
                .updateInterval(0L)
                .functionCall(null)
                .messages(List.of(
                        new GigachatMessage("system", systemContent),
                        new GigachatMessage("user", userContent)
                ))
                .build();
    }

    private void checkAuthorization() {

        if (accessTokenDto == null || accessTokenDto.isExpired()) {
            log.info("Updating token");

            AccessTokenDto accessToken = feignAuthClient.getAccessToken("Bearer " + credentials,
                    UUID.randomUUID().toString(), Map.of("scope", scope));

            this.accessTokenDto = AccessTokenDto
                    .builder()
                    .accessToken("Bearer " + accessToken.accessToken())
                    .expiresAt(accessToken.expiresAt())
                    .build();

            log.info("Access token updated, expires at {}", accessTokenDto.expiresAt());
        }
    }

    private RecognitionResultDto recognizeWithFallback(GigachatRequest request, List<CategoryDto> categories) {

        checkAuthorization();

        try {
            AiReplyDto aiTextDto = feignToGigachatClient.getAiReply(request, accessTokenDto.accessToken());
            log.info("AiReplyDto: {}", aiTextDto);
            String content = extractContent(aiTextDto);

            return parseContent(content, categories);
        } catch (Exception e) {
            log.warn("Primary GigaChat parse failed: {}. Attempting fallback...", e.getMessage());
            meterRegistry.counter("ai.request.fail.count").increment();

            try {
                checkAuthorization();
                GigachatRequest fallbackRequest = buildFallbackRequest(request);
                AiReplyDto fallbackDto = feignToGigachatClient.getAiReply(fallbackRequest, accessTokenDto.accessToken());
                String fallbackContent = extractContent(fallbackDto);
                return parseContent(fallbackContent, categories);
            } catch (Exception fallbackEx) {
                meterRegistry.counter("ai.retry.request.fail.count").increment();
                log.error("Fallback GigaChat parse also failed : {}", fallbackEx.getMessage());
                throw new AiResponseException("Failed to parse both primary and fallback GigaChat responses");
            }
        }
    }

    private String extractContent(AiReplyDto dto) throws JsonProcessingException {

        if (dto != null && !CollectionUtils.isEmpty(dto.choices())) {
            String content = dto.choices().get(0).message().content();
            if (content.startsWith("\"") && content.endsWith("\"")) {
                return objectMapper.readValue(content, String.class);
            }
            return content;
        }
        throw new AiResponseException("Empty GigaChat response");
    }

    private RecognitionResultDto parseContent(String content, List<CategoryDto> categories) throws JsonProcessingException {
        RecognitionResultDto result = objectMapper.readValue(content, RecognitionResultDto.class);
        Set<String> validCategories = categories
                .stream()
                        .map(CategoryDto::getName)
                                .collect(Collectors.toSet());

        result.getProducts().forEach(product -> {
            String category = product.getCategoryName();
            if (category == null || category.isBlank() ||
                validCategories.stream().noneMatch(valid -> valid.equals(category))) {
                log.info("Unknown category from model: '{}', replacing with 'Другое'", category);
                product.setCategoryName("Другое");
            }
        });

        return result;
    }

    private GigachatRequest buildFallbackRequest(GigachatRequest originalRequest) {
        List<GigachatMessage> fallbackMessages = List.of(
                new GigachatMessage("system", PromptUtil.FALLBACK_SYSTEM_CONTENT),
                originalRequest.messages().stream()
                        .filter(m -> "user".equalsIgnoreCase(m.role()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No user message found in original request"))
        );

        return GigachatRequest.builder()
                .model(originalRequest.model())
                .temperature(originalRequest.temperature())
                .variantsCount(originalRequest.variantsCount())
                .maxTokens(originalRequest.maxTokens())
                .repetitionPenalty(originalRequest.repetitionPenalty())
                .stream(originalRequest.stream())
                .updateInterval(originalRequest.updateInterval())
                .functionCall(originalRequest.functionCall())
                .messages(fallbackMessages)
                .build();
    }
}
