package com.marquesdev.stock.controller;

import com.marquesdev.stock.dto.ProductRequestDto;
import com.marquesdev.stock.dto.ProductResponseDto;
import com.marquesdev.stock.dto.ProductUpdateRequestDto;
import com.marquesdev.stock.exception.ErrorMessage;
import com.marquesdev.stock.service.ProductService;
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
@RequestMapping("${app.api.endpoints.products}")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Contains all operations related to product resources for creating, updating, and reading.")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Create a new product",
            description = "Creates a product with its image. Accessible by admins only.",
            security = @SecurityRequirement(name = "security"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(
                                    implementation = ProductMultipartForm.class,
                                    requiredProperties = {
                                            "name", "description", "categoryId", "code",
                                            "unitPrice", "minimumStock", "file"
                                    },
                                    type = "object"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Product created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or unsupported image",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User not authorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Category not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "A product with this code already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Invalid product fields",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponseDto> create(
            @ModelAttribute @Valid ProductRequestDto productRequestDto,
            @RequestPart("file") MultipartFile file) {
        ProductResponseDto product = productService.create(productRequestDto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @Operation(
            summary = "Retrieve a product by ID",
            description = "Retrieves a product by its ID. Accessible by admins and attendants.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User not authorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable UUID id) {
        ProductResponseDto product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Retrieve products with pagination",
            description = "Lists products with pagination and an optional search by name or code. " +
                    "Accessible by admins and attendants. Use the 'search' header to filter products. " +
                    "Default pagination: page=0, size=10, sort=name.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Products retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = ProductResponseDto.class)
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User not authorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    )
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<Page<ProductResponseDto>> findAll(
            @RequestHeader(value = "search", required = false) String search,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable) {
        Page<ProductResponseDto> products = productService.findAll(search, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Update a product",
            description = "Updates product fields and optionally replaces its image. Accessible by admins only.",
            security = @SecurityRequirement(name = "security"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(
                                    implementation = ProductMultipartForm.class,
                                    type = "object"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or unsupported image",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User not authorized",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product or category not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "A product with this code already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Invalid product fields",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable UUID id,
            @ModelAttribute @Valid ProductUpdateRequestDto productUpdateRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        ProductResponseDto product = productService.update(id, productUpdateRequestDto, file);
        return ResponseEntity.ok(product);
    }

    @Schema(name = "ProductMultipartForm")
    public static class ProductMultipartForm {

        @Schema(description = "Product name")
        public String name;

        @Schema(description = "Product description")
        public String description;

        @Schema(description = "ID of the product category")
        public UUID categoryId;

        @Schema(description = "Unique product code")
        public String code;

        @Schema(description = "Product unit price")
        public String unitPrice;

        @Schema(description = "Minimum stock quantity")
        public Integer minimumStock;

        @Schema(type = "string", format = "binary", description = "Product image file")
        public String file;
    }
}
