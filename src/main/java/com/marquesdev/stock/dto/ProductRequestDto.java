package com.marquesdev.stock.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequestDto(

        @NotBlank(message = "Name cannot be blank.")
        @Size(max = 150, message = "Name must contain at most 150 characters.")
        String name,

        @NotBlank(message = "Description cannot be blank.")
        @Size(max = 500, message = "Description must contain at most 500 characters.")
        String description,

        @NotNull(message = "Category ID cannot be null.")
        UUID categoryId,

        @NotBlank(message = "Code cannot be blank.")
        @Size(max = 50, message = "Code must contain at most 50 characters.")
        String code,


        @NotNull(message = "Unit price cannot be null.")
        @DecimalMin(value = "0.01", message = "Unit price must be a positive number.")
        @Digits(integer = 8, fraction = 2, message = "Unit price must be a number with up to 8 digits and 2 decimal places.")
        BigDecimal unitPrice,

        @NotNull(message = "Minimum stock cannot be null.")
        @PositiveOrZero(message = "Minimum stock must be zero or greater.")
        Integer minimumStock

) {
}
