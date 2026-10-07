package com.marquesdev.stock.dto;

import java.util.UUID;

public record ClientRequestDto(

        String name,
        String cnpj,
        String email,
        String phone

) {
}
