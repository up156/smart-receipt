package com.smart_receipt.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "Запрос на регистрацию пользователя")
public record UserRegisterRequest(

        @Email
        String email,

        @NotBlank
        String password,

        @NotBlank
        String fullName

) {
}
