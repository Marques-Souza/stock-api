package com.marquesdev.stock.mapper;

import com.marquesdev.stock.ResourceUrlBuilder.ResourceUrlBuilder;
import com.marquesdev.stock.dto.CategoryRequestDto;
import com.marquesdev.stock.dto.CategoryResponseDto;
import com.marquesdev.stock.dto.CategoryUpdateDto;
import com.marquesdev.stock.entity.Category;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    private final ResourceUrlBuilder resourceUrlBuilder;

    @Value("${app.api.endpoints.categories}")
    private String categoryEndpoint;

    public Category toCategory(CategoryRequestDto dto){
        Category category = new Category();

        category.setName(dto.name());
        category.setDescription(dto.description());
        return category;
    }

    public CategoryResponseDto toDto(Category category){
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                resourceUrlBuilder.build(categoryEndpoint, category.getId())
        );
    }

    public void updateCategory(CategoryUpdateDto dto, Category category){
        if(dto.name() != null) category.setName(dto.name());
        if(dto.description() != null) category.setDescription(dto.description());
    }
}
