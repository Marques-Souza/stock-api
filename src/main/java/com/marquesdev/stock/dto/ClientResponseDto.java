package com.marquesdev.stock.dto;

import java.util.UUID;

public record ClientResponseDto(
        UUID id,
        String name,
        String cnpj,
        String email,
        String phone,
        String url
) {
}
