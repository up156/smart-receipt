package com.smart_receipt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Запрос на обновление продукта")
public record ProductUpdateRequest(
        String name,
        BigDecimal price,
        Long categoryId
) {
}
