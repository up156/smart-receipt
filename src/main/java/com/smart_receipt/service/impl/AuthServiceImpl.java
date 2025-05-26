package com.smart_receipt.service.impl;

import com.smart_receipt.config.security.JwtService;
import com.smart_receipt.config.security.JwtUser;
import com.smart_receipt.dto.TokenResponseDto;
import com.smart_receipt.dto.UserDto;
import com.smart_receipt.ex.UserAlreadyExistException;
import com.smart_receipt.ex.UserUnauthorizedException;
import com.smart_receipt.mapper.UserMapper;
import com.smart_receipt.model.User;
import com.smart_receipt.repository.UserRepository;
import com.smart_receipt.request.EmailLoginRequest;
import com.smart_receipt.request.UserRegisterRequest;
import com.smart_receipt.service.AuthService;
import com.smart_receipt.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CategoryService categoryService;

    private final AuthenticationManager authManager;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private static final String USER_ALREADY_EXISTS_MESSAGE = "Email already in use";
    private static final String UNAUTHORIZED_MESSAGE = "Invalid email or password";

    @Override
    @Transactional(readOnly = true)
    public TokenResponseDto loginByEmail(EmailLoginRequest request) {
        log.info("Auth service started login by Email with request: {}", request);
        try {
            Authentication authentication = authManager.authenticate
                    (new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            JwtUser user = (JwtUser) authentication.getPrincipal();

            return TokenResponseDto.builder()
                    .token(jwtService.generateToken(user))
                    .build();
        } catch (Exception e) {
            throw new UserUnauthorizedException(UNAUTHORIZED_MESSAGE);
        }
    }

    @Override
    @Transactional
    public UserDto register(UserRegisterRequest request) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.email()))) {
            throw new UserAlreadyExistException(USER_ALREADY_EXISTS_MESSAGE);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .isAdmin(Boolean.FALSE)
                .categories(new ArrayList<>())
                .build();

        User savedUser = userRepository.save(user);

        categoryService.createDefaultsFor(savedUser);
        return userMapper.mapToUserDto(userRepository.save(savedUser));
    }
}