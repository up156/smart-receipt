package com.smart_receipt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Запрос на создание категории")
public record CategoryCreateRequest(

        @NotBlank
        String name,
        @NotBlank
        String description,

        BigDecimal monthlyLimit) {
}
