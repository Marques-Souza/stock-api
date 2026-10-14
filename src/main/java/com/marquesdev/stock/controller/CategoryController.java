package com.marquesdev.stock.controller;

import com.marquesdev.stock.dto.CategoryRequestDto;
import com.marquesdev.stock.dto.CategoryResponseDto;
import com.marquesdev.stock.dto.CategoryUpdateDto;
import com.marquesdev.stock.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("${app.api.endpoints.categories}")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CategoryResponseDto> create(
            @ModelAttribute @Valid CategoryRequestDto categoryRequestDto,
            @RequestPart("file") MultipartFile file) {

        CategoryResponseDto category = categoryService.create(categoryRequestDto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CategoryResponseDto> update(
            @PathVariable UUID id,
            @ModelAttribute @Valid CategoryUpdateDto categoryUpdateDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        CategoryResponseDto category = categoryService.update(id, categoryUpdateDto, file);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<CategoryResponseDto> findById(@PathVariable UUID id){
        CategoryResponseDto category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<Page<CategoryResponseDto>> findAll(
            @RequestHeader(value = "search", required = false) String search,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ){
        Page<CategoryResponseDto> categories = categoryService.findAll(search, pageable);
        return  ResponseEntity.ok(categories);

    }

}
