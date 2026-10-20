package com.marquesdev.stock.dto;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String email,
        String profile,
        String url
) {
}
