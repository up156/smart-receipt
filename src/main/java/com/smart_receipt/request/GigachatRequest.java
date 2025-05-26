package com.smart_receipt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;


@Builder
@Schema(description = "Запрос на распознавание чека пользователя")
public record GigachatRequest(

        @JsonProperty("model")
        String model,
        @JsonProperty("temperature")
        Double temperature,
        @JsonProperty("n")
        Long variantsCount,
        @JsonProperty("max_tokens")
        Long maxTokens,
        @JsonProperty("repetition_penalty")
        Double repetitionPenalty,
        @JsonProperty("stream")
        Boolean stream,
        @JsonProperty("update_interval")
        Long updateInterval,
        @JsonProperty("function_call")
        String functionCall,
        @JsonProperty("messages")
        List<GigachatMessage> messages


) {
}


