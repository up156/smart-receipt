package com.smart_receipt.controller;

import com.smart_receipt.config.security.JwtUser;
import com.smart_receipt.dto.CategoryDto;
import com.smart_receipt.request.CategoryCreateRequest;
import com.smart_receipt.request.CategoryUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("api/v1/category")
@Tag(name = "CategoryController", description = "Контроллер для работы с категориями продуктов")
public interface CategoryController {

    @GetMapping
    @Operation(summary = "Получить все категории текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class)))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<List<CategoryDto>> getAllCategories(@Parameter(hidden = true) @AuthenticationPrincipal JwtUser jwtUser);

    @PostMapping
    @Operation(summary = "Создать категорию для текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class)))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<CategoryDto> createCategory(@Parameter(hidden = true) @AuthenticationPrincipal JwtUser jwtUser,
                                               @RequestBody CategoryCreateRequest request);

    @PutMapping("/{id}")
    @Operation(summary = "Изменить категорию для текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class)))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<CategoryDto> updateCategory(@Parameter(hidden = true) @AuthenticationPrincipal JwtUser jwtUser,
                                               @PathVariable Long id, @RequestBody CategoryUpdateRequest request);

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить категорию для текущего пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Операция проведена успешно",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class)))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    ResponseEntity<Void> deleteCategory(@Parameter(hidden = true) @AuthenticationPrincipal JwtUser jwtUser,
                                        @PathVariable Long id);
}