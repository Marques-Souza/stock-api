package com.marquesdev.stock.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ClientUpdateRequestDto(


        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters.")
        String name,


        @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid email format.")
        String email,


        @Size(max = 20, message = "Phone number must contain a maximum of 20 characters.")
        String phone

) {
}
