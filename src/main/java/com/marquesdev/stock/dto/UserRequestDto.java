package com.marquesdev.stock.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDto(

        @NotBlank(message = "Email cannot be blank.")
        @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid email format.")
        String email,

        @NotBlank(message = "Password cannot be blank.")
        @Size(min = 5, max = 10, message = "Password must be between 5 and 10 characters.")
        String password,

        @NotBlank(message = "Profile cannot be blank.")
        String profile
) {
}
