package com.marquesdev.stock.mapper;


import com.marquesdev.stock.dto.StockMovementRequestDto;
import com.marquesdev.stock.dto.StockMovementResponseDto;
import com.marquesdev.stock.entity.Client;
import com.marquesdev.stock.entity.Product;
import com.marquesdev.stock.entity.StockMovement;
import com.marquesdev.stock.entity.Supplier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StockMovementMapper {

    public StockMovement toEntity(StockMovementRequestDto dto,
                                  Product product,
                                  Supplier supplier,
                                  Client client){
        StockMovement movement = new StockMovement();

        movement.setProduct(product);
        movement.setType(dto.type());
        movement.setQuantity(dto.quantity());
        movement.setUnitPrice(product.getUnitPrice());
        movement.setSupplier(supplier);
        movement.setClient(client);
        movement.setNote(dto.note());

        return movement;
    }

    public StockMovementResponseDto toDto(StockMovement movement){
        return new StockMovementResponseDto(
                movement.getId(),
                movement.getMovementCode(),
                movement.getProduct().getId(),
                movement.getProduct().getName(),
                movement.getType(),
                movement.getQuantity(),
                movement.getUnitPrice(),
                movement.getUnitPrice().multiply(BigDecimal.valueOf(movement.getQuantity())),
                movement.getNote(),
                movement.getSupplier() != null ? movement.getSupplier().getId() : null,
                movement.getSupplier() != null ? movement.getSupplier().getName() : null,
                movement.getClient() != null ? movement.getClient().getId() : null,
                movement.getClient() != null ? movement.getClient().getName() : null,
                movement.getCreatedAt(),
                movement.getCreatedBy()
        );
    }
}
