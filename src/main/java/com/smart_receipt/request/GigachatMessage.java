package com.smart_receipt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Builder
@Schema(description = "Дто для подготовки запроса")
public record GigachatMessage(
        String role,
        String content
) {
}


