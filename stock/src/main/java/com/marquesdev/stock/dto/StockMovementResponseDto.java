package com.marquesdev.stock.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.marquesdev.stock.enums.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StockMovementResponseDto(

        UUID id,
        String movementCode,
        UUID productId,
        String productName,
        Type type,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalValue,
        String note,
        UUID supplierId,
        String supplierName,
        UUID clientId,
        String clientName,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        String createdBy

) {
}
