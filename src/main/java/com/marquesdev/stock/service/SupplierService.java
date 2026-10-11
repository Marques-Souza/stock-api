package com.marquesdev.stock.service;

import com.marquesdev.stock.dto.SupplierRequestDto;
import com.marquesdev.stock.dto.SupplierResponseDto;
import com.marquesdev.stock.dto.SupplierUpdateDto;
import com.marquesdev.stock.entity.Supplier;
import com.marquesdev.stock.exception.CnpjUniqueException;
import com.marquesdev.stock.exception.EmailUniqueException;
import com.marquesdev.stock.exception.EntityNotFoundException;
import com.marquesdev.stock.mapper.SupplierMapper;
import com.marquesdev.stock.repository.SupplierRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Transactional
    public SupplierResponseDto saveSupplier(SupplierRequestDto supplierRequestDto){
        if (supplierRepository.existsByEmail(supplierRequestDto.email())){
            throw new EmailUniqueException("Email already exists.");
        }

        if (supplierRepository.existsByCnpj(supplierRequestDto.cnpj())){
            throw new CnpjUniqueException("CNPJ already exists.");
        }

        Supplier supplier = supplierMapper.toSupplier(supplierRequestDto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }

    @Transactional(readOnly = true)
    public SupplierResponseDto findById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
        return supplierMapper.toDto(supplier);
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponseDto> findAll(String search, Pageable pageable){
        Page<Supplier> suppliersPage = supplierRepository.findAll(pageable);

        if (search != null && !search.isBlank()){
            suppliersPage = supplierRepository.findBySearch(search, pageable);
        }
        return suppliersPage.map(supplierMapper::toDto);
    }


    @Transactional
    public SupplierResponseDto updateSupplier(UUID id, @Valid SupplierUpdateDto supplierUpdateDto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
        supplierMapper.updateSupplier(supplierUpdateDto, supplier);
        Supplier updatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(updatedSupplier);
    }
}
