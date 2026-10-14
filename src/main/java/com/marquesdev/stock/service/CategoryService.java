package com.marquesdev.stock.service;

import com.marquesdev.stock.dto.CategoryRequestDto;
import com.marquesdev.stock.dto.CategoryResponseDto;
import com.marquesdev.stock.dto.CategoryUpdateDto;
import com.marquesdev.stock.entity.Category;
import com.marquesdev.stock.exception.CategoryAlreadyExistsException;
import com.marquesdev.stock.exception.EntityNotFoundException;
import com.marquesdev.stock.mapper.CategoryMapper;
import com.marquesdev.stock.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ImageService imageService;

    @Transactional
    public CategoryResponseDto create(CategoryRequestDto categoryRequestDto, MultipartFile file) {
        if (categoryRepository.existsByNameIgnoreCase(categoryRequestDto.name())){
            throw new CategoryAlreadyExistsException("Category Already exists.");
        }
        Category category = categoryMapper.toCategory(categoryRequestDto);
        String imageKey = imageService.store(file, "categories");
        category.setImageUrl(imageKey);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto findById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found."));
        return categoryMapper.toDto(category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> findAll(String search, Pageable pageable) {
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);

        if (search != null && !search.isBlank()){
            categoriesPage = categoryRepository.findBySearch(search, pageable);
        }
        return categoriesPage.map(categoryMapper::toDto);
    }

    @Transactional
    public CategoryResponseDto update(
            UUID id,
            CategoryUpdateDto categoryUpdateDto,
            MultipartFile file) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found."));

        if (!category.getName().equalsIgnoreCase(categoryUpdateDto.name())
                && categoryRepository.existsByNameIgnoreCase(categoryUpdateDto.name())) {
            throw new CategoryAlreadyExistsException("Category already exists.");
        }

        categoryMapper.updateCategory(categoryUpdateDto, category);

        if (file != null && !file.isEmpty()) {
            String newImageKey = imageService.replace(
                    category.getImageUrl(),
                    file,
                    "categories"
            );
            category.setImageUrl(newImageKey);
        }

        return categoryMapper.toDto(categoryRepository.save(category));
    }

}
