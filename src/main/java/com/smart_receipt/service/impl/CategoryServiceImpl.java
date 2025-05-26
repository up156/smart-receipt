package com.smart_receipt.service.impl;

import com.smart_receipt.config.security.JwtUser;
import com.smart_receipt.dto.CategoryDto;
import com.smart_receipt.ex.CategoryAlreadyExistException;
import com.smart_receipt.ex.CategoryNotFoundException;
import com.smart_receipt.ex.UserForbiddenException;
import com.smart_receipt.ex.UserNotFoundException;
import com.smart_receipt.mapper.CategoryMapper;
import com.smart_receipt.model.Category;
import com.smart_receipt.model.CategoryStats;
import com.smart_receipt.model.User;
import com.smart_receipt.repository.CategoryRepository;
import com.smart_receipt.repository.ProductRepository;
import com.smart_receipt.repository.UserRepository;
import com.smart_receipt.request.CategoryCreateRequest;
import com.smart_receipt.request.CategoryUpdateRequest;
import com.smart_receipt.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    private final CategoryMapper categoryMapper;

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found";
    private static final String NO_AUTHORITY_MESSAGE = "User does not have authority";
    private static final String CATEGORY_ALREADY_EXISTS_MESSAGE = "Category with such name is already exists: ";


    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(JwtUser jwtUser) {

        log.info("Category service started get all categories for user: {}", jwtUser);
        List<Category> list = categoryRepository.findByUser_Id(jwtUser.getId());
        return list.stream().map(categoryMapper::mapToCategoryDto).toList();
    }

    @Override
    @Transactional
    public CategoryDto createCategory(JwtUser jwtUser, CategoryCreateRequest request) {

        log.info("Category service started create category for user: {} with request: {}", jwtUser, request);
        if (request.name() != null && categoryRepository.existsByNameAndUser_Id(request.name(), jwtUser.getId())) {
            throw new CategoryAlreadyExistException(CATEGORY_ALREADY_EXISTS_MESSAGE + request.name());
        }

        LocalDate now = LocalDate.now();
        User user = userRepository.findById(jwtUser.getId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        CategoryStats categoryStats = CategoryStats
                .builder()
                .month(now.getMonthValue())
                .year(now.getYear())
                .build();

        Category category = Category
                .builder()
                .name(request.name())
                .description(request.description())
                .user(user)
                .monthlyLimit(request.monthlyLimit())
                .stats(List.of(categoryStats))
                .build();

        categoryStats.setCategory(category);

        return categoryMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @CacheEvict(value = "receipts", allEntries = true)
    public CategoryDto updateCategory(JwtUser jwtUser, Long id, CategoryUpdateRequest request) {

        log.info("Category service started update category for user: {} with request: {}", jwtUser, request);
        if (request.name() != null && categoryRepository.existsByNameAndUser_Id(request.name(), jwtUser.getId())) {
            throw new CategoryAlreadyExistException(CATEGORY_ALREADY_EXISTS_MESSAGE + request.name());
        }

        Category category = getUserCategoryOrThrow(id, jwtUser.getId());
        categoryMapper.updateCategory(category, request);

        return categoryMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @CacheEvict(value = "receipts", allEntries = true)
    public void deleteCategory(JwtUser jwtUser, Long id) {

        log.info("Category service started delete category for user: {}", jwtUser);
        Category category = getUserCategoryOrThrow(id, jwtUser.getId());
        productRepository.clearCategoryByCategoryId(category.getId());
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public void createDefaultsFor(User user) {

        log.info("Category service started create default categories for user email: {}", user.getEmail());

        List<Category> defaults = List.of(
                Category
                        .builder()
                        .name("Овощи и фрукты")
                        .description("Свежие овощи, фрукты, зелень")
                        .user(user)
                        .build(),
                Category
                        .builder()
                        .name("Молочные продукты")
                        .description("Молоко, сыр, йогурты")
                        .user(user)
                        .build(),
                Category
                        .builder()
                        .name("Хлебобулочные изделия")
                        .description("Хлеб, булки")
                        .user(user)
                        .build(),
                Category
                        .builder()
                        .name("Мясо, птица, рыба")
                        .description("Свинина, курица, говядина, рыба")
                        .user(user)
                        .build(),
                Category
                        .builder()
                        .name("Другое")
                        .description("Все остальное")
                        .user(user)
                        .build(),
                Category
                        .builder()
                        .name("Сладости, кондитерский")
                        .description("Шоколад, конфеты, печенье")
                        .user(user)
                        .build(),
                Category
                        .builder()
                        .name("Напитки")
                        .description("Сок, вода, чай, кофе")
                        .user(user)
                        .build(),
                Category
                        .builder()
                        .name("Бакалея")
                        .description("Рис, гречка, макароны")
                        .user(user)
                        .build()
        );

        categoryRepository.saveAll(defaults);
    }

    private Category getUserCategoryOrThrow(Long categoryId, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));
        if (!Objects.equals(category.getUser().getId(), user.getId())) {
            throw new UserForbiddenException(NO_AUTHORITY_MESSAGE);
        }
        return category;
    }
}