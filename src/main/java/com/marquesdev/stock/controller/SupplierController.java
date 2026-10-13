package com.marquesdev.stock.controller;

import com.marquesdev.stock.dto.SupplierRequestDto;
import com.marquesdev.stock.dto.SupplierResponseDto;
import com.marquesdev.stock.dto.SupplierUpdateDto;
import com.marquesdev.stock.exception.ErrorMessage;
import com.marquesdev.stock.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Suppliers", description = "Contains all operations related to supplier resources for creating, updating, and reading.")
@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.endpoints.suppliers}")
public class SupplierController {

    private final SupplierService supplierService;


    @Operation(summary = "Create a new supplier",
            description = "Endpoint to create a new supplier accessible by the admin (Bearer token required)",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Supplier created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Email or CNPJ already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SupplierResponseDto> createSupplier(@Valid @RequestBody SupplierRequestDto supplierRequestDto){
        SupplierResponseDto supplier = supplierService.saveSupplier(supplierRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(supplier);
    }

    @Operation(summary = "Retrieve a supplier by ID",
            description = "Endpoint to get supplier by ID (Bearer token required)",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Supplier retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Supplier not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<SupplierResponseDto> findById(@PathVariable UUID id){
        SupplierResponseDto supplier = supplierService.findById(id);
        return ResponseEntity.ok(supplier);
    }

    @Operation(summary = "Retrieve all suppliers with pagination",
            description = "Endpoint to list all suppliers with pagination and search filter accessible by admin and attendant (Bearer token required). " +
                    "Use 'search' header to filter by name. Default pagination: page=0, size=10, sort=name",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Suppliers retrieved successfully",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SupplierResponseDto.class)))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<Page<SupplierResponseDto>> findAll(
            @RequestHeader(value = "search", required = false)String search,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ){
        Page<SupplierResponseDto> suppliers = supplierService.findAll(search, pageable);
        return ResponseEntity.ok(suppliers);
    }

    @Operation(summary = "Update a supplier",
            description = "Endpoint to update supplier information accessible by the admin (Bearer token required)",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Supplier updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Supplier not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SupplierResponseDto> updateSupplier(@PathVariable UUID id, @Valid @RequestBody SupplierUpdateDto supplierUpdateDto){
        SupplierResponseDto supplier = supplierService.updateSupplier(id, supplierUpdateDto);
        return ResponseEntity.ok(supplier);
    }
}
