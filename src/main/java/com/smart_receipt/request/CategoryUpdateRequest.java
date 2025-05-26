package com.smart_receipt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Запрос на обновление категории")
public record CategoryUpdateRequest(
        String name,
        String description,
        BigDecimal monthlyLimit) {
}
