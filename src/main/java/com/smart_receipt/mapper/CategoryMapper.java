package com.smart_receipt.mapper;

import com.smart_receipt.dto.CategoryDto;
import com.smart_receipt.dto.CategoryStatsDto;
import com.smart_receipt.model.Category;
import com.smart_receipt.model.CategoryStats;
import com.smart_receipt.request.CategoryUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto mapToCategoryDto(Category category);

    @Mapping(target = "categoryName", source = "categoryStats.category.name")
    CategoryStatsDto mapToCategoryStatsDto(CategoryStats categoryStats);

    default void updateCategory(Category toUpdate, CategoryUpdateRequest request) {
        if (request.name() != null) {
            toUpdate.setName(request.name());
        }
        if (request.description() != null) {
            toUpdate.setDescription(request.description());
        }
        if (request.monthlyLimit() != null) {
            toUpdate.setMonthlyLimit(request.monthlyLimit());
        }
    }
}
