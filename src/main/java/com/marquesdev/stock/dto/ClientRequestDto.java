package com.marquesdev.stock.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClientRequestDto(

        @NotBlank(message = "Name cannot be blank.")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters.")
        String name,

        @NotBlank(message = "CNPJ cannot be blank.")
        @Pattern(regexp = "^\\d{14}$", message = "CNPJ must contain exactly 14 digits.")
        String cnpj,

        @NotBlank(message = "Email cannot be blank.")
        @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid email format.")
        String email,

        @NotBlank(message = "Phone cannot be blank.")
        @Size(max = 20, message = "Phone number must contain a maximum of 20 characters.")
        String phone

) {
}
