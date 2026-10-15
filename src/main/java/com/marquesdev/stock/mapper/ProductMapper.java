package com.marquesdev.stock.mapper;

import com.marquesdev.stock.ResourceUrlBuilder.ResourceUrlBuilder;
import com.marquesdev.stock.dto.ProductRequestDto;
import com.marquesdev.stock.dto.ProductResponseDto;
import com.marquesdev.stock.dto.ProductUpdateRequestDto;
import com.marquesdev.stock.entity.Category;
import com.marquesdev.stock.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ResourceUrlBuilder resourceUrlBuilder;

    @Value("${app.api.endpoints.products}")
    private String productEndpoint;

    public Product toProduct(ProductRequestDto dto, Category category){
        Product product = new Product();

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategory(category);
        product.setCode(dto.code());
        product.setUnitPrice(dto.unitPrice());
        product.setMinimumStock(dto.minimumStock());

        return  product;
    }

    public ProductResponseDto toDto(Product product){
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCode(),
                product.getQuantity(),
                product.getUnitPrice(),
                product.getMinimumStock(),
                resourceUrlBuilder.build(productEndpoint, product.getId()),
                resourceUrlBuilder.buildImageUrl(product.getImageUrl())
        );
    }

    public void updateProduct(
            ProductUpdateRequestDto dto,
            Product product,
            Category category
    ){
      if (dto.name() != null) product.setName(dto.name());
      if (dto.description() != null) product.setDescription(dto.description());
      if (dto.categoryId() != null) product.setCategory(category);
      if (dto.code() != null) product.setCode(dto.code());
      if (dto.unitPrice() != null) product.setUnitPrice(dto.unitPrice());
      if (dto.minimumStock() != null) product.setMinimumStock(dto.minimumStock());
    }
}
