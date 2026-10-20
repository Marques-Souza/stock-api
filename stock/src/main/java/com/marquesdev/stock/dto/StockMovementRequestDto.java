package com.marquesdev.stock.dto;

import com.marquesdev.stock.enums.Type;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record StockMovementRequestDto(

        @NotNull
        UUID productId,

        @NotNull
        Type type,

        @NotNull
        @Positive
        Integer quantity,

        @Size(max = 500)
        String note,

        UUID supplierId,
        UUID clientId
) {
}
