package com.smart_receipt.service;

import com.smart_receipt.config.security.JwtUser;
import com.smart_receipt.dto.CategoryDto;
import com.smart_receipt.model.User;
import com.smart_receipt.request.CategoryCreateRequest;
import com.smart_receipt.request.CategoryUpdateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;


@Tag(name = "CategoryService", description = "Сервис для работы с категориями пользователя")
public interface CategoryService {

    List<CategoryDto> getAllCategories(JwtUser jwtUser);

    CategoryDto createCategory(JwtUser jwtUser, CategoryCreateRequest request);

    CategoryDto updateCategory(JwtUser jwtUser, Long id, CategoryUpdateRequest request);

    void deleteCategory(JwtUser jwtUser, Long id);

    void createDefaultsFor(User user);
}