package com.smart_receipt.controller.impl;

import com.smart_receipt.controller.AuthController;
import com.smart_receipt.dto.TokenResponseDto;
import com.smart_receipt.dto.UserDto;
import com.smart_receipt.request.EmailLoginRequest;
import com.smart_receipt.request.UserRegisterRequest;
import com.smart_receipt.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    public ResponseEntity<TokenResponseDto> loginByEmail(@RequestBody @Valid EmailLoginRequest request) {
        return ResponseEntity.ok(authService.loginByEmail(request));
    }

    @Override
    public ResponseEntity<UserDto> register(UserRegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
