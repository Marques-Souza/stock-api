package com.marquesdev.stock.mapper;

import com.marquesdev.stock.ResourceUrlBuilder.ResourceUrlBuilder;
import com.marquesdev.stock.dto.SupplierRequestDto;
import com.marquesdev.stock.dto.SupplierResponseDto;
import com.marquesdev.stock.dto.SupplierUpdateDto;
import com.marquesdev.stock.entity.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SupplierMapper {

    private final ResourceUrlBuilder resourceUrlBuilder;

    @Value("${app.api.endpoints.suppliers}")
    private String supplierEndpoint;


    public Supplier toSupplier(SupplierRequestDto dto){
        Supplier supplier = new Supplier();
        supplier.setName(dto.name());
        supplier.setCnpj(dto.cnpj());
        supplier.setEmail(dto.email());
        supplier.setPhone(dto.phone());

        return supplier;
    }

    public SupplierResponseDto toDto(Supplier supplier){
        return new SupplierResponseDto(
                supplier.getId(),
                supplier.getName(),
                supplier.getCnpj(),
                supplier.getEmail(),
                supplier.getPhone(),
                resourceUrlBuilder.build(supplierEndpoint, supplier.getId())
        );
    }

    public void updateSupplier(SupplierUpdateDto dto, Supplier supplier){
        if (dto.name() != null) supplier.setName(dto.name());
        if (dto.email() != null) supplier.setEmail(dto.email());
        if (dto.phone() != null) supplier.setPhone(dto.phone());
    }


}
