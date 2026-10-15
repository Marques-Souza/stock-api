package com.marquesdev.stock.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDto(

        UUID id, String name,
        String description,
        UUID categoryId,
        String categoryName,
        String code,
        Integer quantity,
        BigDecimal unitPrice,
        Integer minimumStock,
        String url,
        String imageUrl
) {
}
