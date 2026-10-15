package com.marquesdev.stock.service;


import com.marquesdev.stock.dto.ProductRequestDto;
import com.marquesdev.stock.dto.ProductResponseDto;
import com.marquesdev.stock.dto.ProductUpdateRequestDto;
import com.marquesdev.stock.entity.Category;
import com.marquesdev.stock.entity.Product;
import com.marquesdev.stock.exception.EntityNotFoundException;
import com.marquesdev.stock.exception.ProductAlreadyExistsException;
import com.marquesdev.stock.mapper.ProductMapper;
import com.marquesdev.stock.repository.CategoryRepository;
import com.marquesdev.stock.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ImageService imageService;
    private final CategoryRepository categoryRepository;


    public ProductResponseDto create(
            ProductRequestDto productRequestDto,
            MultipartFile file
    ) {
        if (productRepository.existsByCode(productRequestDto.code())) {
            throw new ProductAlreadyExistsException("Product code already exists.");
        }

        Category category = categoryRepository.findById(productRequestDto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Product product = productMapper.toProduct(
                productRequestDto,
                category
        );

        String imageKey = imageService.store(file, "products");
        product.setImageUrl(imageKey);

        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found."));
        return productMapper.toDto(product);
    }

    public Page<ProductResponseDto> findAll(String search, Pageable pageable) {
        Page<Product> productsPage = productRepository.findAll(pageable);

        if (search != null && !search.isBlank()) {
            productsPage = productRepository.findBySearch(search, pageable);
        }
        return productsPage.map(productMapper::toDto);
    }

    @Transactional
    public ProductResponseDto update(
            UUID id,
            ProductUpdateRequestDto productUpdateRequestDto,
            MultipartFile file) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found."));

        if (productUpdateRequestDto.code() != null
                && !product.getCode().equalsIgnoreCase(productUpdateRequestDto.code())
                && productRepository.existsByCodeAndIdNot(productUpdateRequestDto.code(), id)) {
            throw new ProductAlreadyExistsException("Product code already exists.");
        }

        Category category = product.getCategory();
        if (productUpdateRequestDto.categoryId() != null) {
            category = categoryRepository.findById(productUpdateRequestDto.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found."));
        }
        productMapper.updateProduct(productUpdateRequestDto, product, category);

        if (file != null && !file.isEmpty()) {
            String newImageKey = imageService.replace(
                    product.getImageUrl(),
                    file,
                    "products"
            );
            product.setImageUrl(newImageKey);
        }

        return productMapper.toDto(productRepository.save(product));
    }
}
