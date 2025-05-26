package com.smart_receipt.controller;

import com.smart_receipt.dto.TokenResponseDto;
import com.smart_receipt.dto.UserDto;
import com.smart_receipt.request.EmailLoginRequest;
import com.smart_receipt.request.UserRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("api/v1/auth")
@Tag(name = "AuthController", description = "Контроллер для работы с авторизацией пользователя")
public interface AuthController {

    @PostMapping("/login")
    @Operation(summary = "Получение токена по email + пароль")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Токен получен",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenResponseDto.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<TokenResponseDto> loginByEmail(@RequestBody @Valid EmailLoginRequest request);

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<UserDto> register(@RequestBody @Valid UserRegisterRequest request);
}