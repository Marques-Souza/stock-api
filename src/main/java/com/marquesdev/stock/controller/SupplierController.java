package com.marquesdev.stock.controller;

import com.marquesdev.stock.dto.SupplierRequestDto;
import com.marquesdev.stock.dto.SupplierResponseDto;
import com.marquesdev.stock.dto.SupplierUpdateDto;
import com.marquesdev.stock.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.endpoints.suppliers}")
public class SupplierController {

    private final SupplierService supplierService;


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SupplierResponseDto> createSupplier(@Valid @RequestBody SupplierRequestDto supplierRequestDto){
        SupplierResponseDto supplier = supplierService.saveSupplier(supplierRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(supplier);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('USER')")
    public ResponseEntity<SupplierResponseDto> findById(@PathVariable UUID id){
        SupplierResponseDto supplier = supplierService.findById(id);
        return ResponseEntity.ok(supplier);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('USER')")
    public ResponseEntity<Page<SupplierResponseDto>> findAll(
            @RequestHeader(value = "search", required = false)String search,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ){
        Page<SupplierResponseDto> suppliers = supplierService.findAll(search, pageable);
        return ResponseEntity.ok(suppliers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SupplierResponseDto> updateSupplier(@PathVariable UUID id, @Valid @RequestBody SupplierUpdateDto supplierUpdateDto){
        SupplierResponseDto supplier = supplierService.updateSupplier(id, supplierUpdateDto);
        return ResponseEntity.ok(supplier);
    }
}
