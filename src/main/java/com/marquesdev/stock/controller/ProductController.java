package com.marquesdev.stock.controller;

import com.marquesdev.stock.dto.CategoryResponseDto;
import com.marquesdev.stock.dto.ProductRequestDto;
import com.marquesdev.stock.dto.ProductResponseDto;
import com.marquesdev.stock.dto.ProductUpdateRequestDto;
import com.marquesdev.stock.service.ProductService;
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
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponseDto> create(
            @ModelAttribute @Valid ProductRequestDto productRequestDto,
            @RequestPart("file") MultipartFile file
            ){
        ProductResponseDto product = productService.create(productRequestDto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);

    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable UUID id){
        ProductResponseDto product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<Page<ProductResponseDto>> findAll(
            @RequestHeader(value = "search", required = false) String search,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
            ){
        Page<ProductResponseDto> products = productService.findAll(search, pageable);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable UUID id,
            @ModelAttribute @Valid ProductUpdateRequestDto productUpdateRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        ProductResponseDto product = productService.update(id, productUpdateRequestDto, file);
        return ResponseEntity.ok(product);
    }

}
