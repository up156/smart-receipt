package com.smart_receipt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Дто для обработки исключений")
public record ErrorDto(

        String message,
        Integer statusCode
) {
}
