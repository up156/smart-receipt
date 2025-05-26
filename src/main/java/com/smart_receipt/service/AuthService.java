package com.smart_receipt.service;

import com.smart_receipt.dto.TokenResponseDto;
import com.smart_receipt.dto.UserDto;
import com.smart_receipt.request.EmailLoginRequest;
import com.smart_receipt.request.UserRegisterRequest;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "AuthService", description = "Сервис для работы с авторизацией пользователя")
public interface AuthService {

    TokenResponseDto loginByEmail(EmailLoginRequest request);

    UserDto register(UserRegisterRequest request);
}