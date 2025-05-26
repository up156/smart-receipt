package com.smart_receipt.controller;

import com.smart_receipt.config.security.JwtUser;
import com.smart_receipt.dto.CategoryStatsDto;
import com.smart_receipt.dto.ProductDto;
import com.smart_receipt.dto.ReceiptDto;
import com.smart_receipt.request.ProductUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequestMapping("api/v1/receipt")
@Tag(name = "ReceiptController", description = "Контроллер для работы с чеками")
public interface ReceiptController {

    @PostMapping("/upload-image")
    @Operation(summary = "Операция обработки фото чека (OCR + AI + Сохранение)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReceiptDto.class))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<ReceiptDto> uploadReceiptImage(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUser jwtUser,
            @RequestParam("file") MultipartFile file);

    @PostMapping("/upload-text")
    @Operation(summary = "Операция обработки текста чека (AI + Сохранение)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReceiptDto.class))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<ReceiptDto> uploadReceiptText(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUser jwtUser,
            @RequestBody String text);

    @GetMapping
    @Operation(summary = "Получить все чеки текущего пользователя с фильтрами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ReceiptDto.class)))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<List<ReceiptDto>> getAllReceipts(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUser jwtUser,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to);

    @GetMapping("/{receiptId}")
    @Operation(summary = "Получение чека по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProductDto.class)))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<ReceiptDto> getReceipt(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable Long receiptId);

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Обновление продукта по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<ProductDto> updateProduct(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable Long itemId, @RequestBody ProductUpdateRequest request);

    @GetMapping("/stats")
    @Operation(summary = "Получение статистики по категориям")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoryStatsDto.class)))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<List<CategoryStatsDto>> getStats(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUser jwtUser,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            @RequestParam(required = false) @Min(2000) @Max(2100) Integer year);
}
