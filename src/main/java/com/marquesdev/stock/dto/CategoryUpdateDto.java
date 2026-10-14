package com.marquesdev.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpdateDto(

        @Size(max = 200, message = "Name must contain at most 200 characters.")
        String name,

        @Size(max = 200, message = "Description  must contain at most 200 characters.")
        String description
) {
}
