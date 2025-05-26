package com.smart_receipt.controller.impl;

import com.smart_receipt.config.security.JwtUser;
import com.smart_receipt.controller.CategoryController;
import com.smart_receipt.dto.CategoryDto;
import com.smart_receipt.request.CategoryCreateRequest;
import com.smart_receipt.request.CategoryUpdateRequest;
import com.smart_receipt.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryControllerImpl implements CategoryController {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity<List<CategoryDto>> getAllCategories(JwtUser jwtUser) {
        return ResponseEntity.ok(categoryService.getAllCategories(jwtUser));
    }

    @Override
    public ResponseEntity<CategoryDto> createCategory(JwtUser jwtUser, CategoryCreateRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(jwtUser, request));
    }

    @Override
    public ResponseEntity<CategoryDto> updateCategory(JwtUser jwtUser, Long id, CategoryUpdateRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(jwtUser, id, request));
    }

    @Override
    public ResponseEntity<Void> deleteCategory(JwtUser jwtUser, Long id) {
        categoryService.deleteCategory(jwtUser, id);
        return ResponseEntity.ok().build();
    }
}
