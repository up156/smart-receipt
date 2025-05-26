package com.smart_receipt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "Запрос на логин по email")
public record EmailLoginRequest(

        @Email
        @NotBlank
        String email,

        @NotBlank
        String password
) {
}
