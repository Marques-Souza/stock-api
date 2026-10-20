package com.marquesdev.stock.dto;

import java.util.UUID;

public record CategoryResponseDto(

        UUID id,
        String name,
        String description,
        String url,
        String imageUrl

) {
}
