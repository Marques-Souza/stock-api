package com.marquesdev.stock.controller;

import com.marquesdev.stock.dto.CategoryRequestDto;
import com.marquesdev.stock.dto.CategoryResponseDto;
import com.marquesdev.stock.dto.CategoryUpdateDto;
import com.marquesdev.stock.exception.ErrorMessage;
import com.marquesdev.stock.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Categories", description = "Contains all operations related to category resources for creating, updating, and reading.")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create a new category",
            description = "Creates a category with its image. Accessible by admins only.",
            security = @SecurityRequirement(name = "security"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(
                                    implementation = CategoryMultipartForm.class,
                                    requiredProperties = {"name", "description", "file"},
                                    type = "object"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Category created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "A category with this name already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid category fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CategoryResponseDto> create(
            @ModelAttribute @Valid CategoryRequestDto categoryRequestDto,
            @RequestPart("file") MultipartFile file) {

        CategoryResponseDto category = categoryService.create(categoryRequestDto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @Operation(summary = "Update a category",
            description = "Updates category fields and optionally replaces its image. Accessible by admins only.",
            security = @SecurityRequirement(name = "security"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = CategoryMultipartForm.class, type = "object")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "A category with this name already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid category fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CategoryResponseDto> update(
            @PathVariable UUID id,
            @ModelAttribute @Valid CategoryUpdateDto categoryUpdateDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        CategoryResponseDto category = categoryService.update(id, categoryUpdateDto, file);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Retrieve a category by ID",
            description = "Retrieves a category by its ID. Accessible by admins and attendants.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<CategoryResponseDto> findById(@PathVariable UUID id){
        CategoryResponseDto category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Retrieve categories with pagination",
            description = "Lists categories with pagination and an optional name search. Accessible by admins and attendants. " +
                    "Use the 'search' header to filter by category name. Default pagination: page=0, size=10, sort=name.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDto.class)))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<Page<CategoryResponseDto>> findAll(
            @RequestHeader(value = "search", required = false) String search,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ){
        Page<CategoryResponseDto> categories = categoryService.findAll(search, pageable);
        return  ResponseEntity.ok(categories);

    }

    @Schema(name = "CategoryMultipartForm")
    public static class CategoryMultipartForm {

        @Schema(description = "Category name")
        public String name;

        @Schema(description = "Category description")
        public String description;

        @Schema(type = "string", format = "binary", description = "Category image file")
        public String file;
    }

}
