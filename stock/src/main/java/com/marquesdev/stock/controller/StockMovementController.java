package com.marquesdev.stock.controller;

import com.marquesdev.stock.dto.StockMovementRequestDto;
import com.marquesdev.stock.dto.StockMovementResponseDto;
import com.marquesdev.stock.enums.Type;
import com.marquesdev.stock.exception.ErrorMessage;
import com.marquesdev.stock.service.StockMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.endpoints.stock-movements}")
@Tag(name = "Stock movements", description = "Contains operations for creating and retrieving stock movements.")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @Operation(summary = "Create a stock movement",
            description = "Registers an entry or exit and updates product stock. Entries require a supplier; exits require a client. " +
                    "Accessible by admins and attendants.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Stock movement created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StockMovementResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Product, supplier, or client not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Insufficient stock for the requested exit",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid fields or stock movement configuration",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<StockMovementResponseDto> createStockMovement(@Valid @RequestBody StockMovementRequestDto stockMovementRequestDto){
        StockMovementResponseDto movement = stockMovementService.createMovement(stockMovementRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(movement);
    }

    @Operation(summary = "Retrieve stock movements with pagination",
            description = "Lists stock movements, optionally filtered by movement type. " +
                    "Accessible by admins and attendants. Default pagination: page=0, size=10, sort=createdAt, direction=DESC.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stock movements retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = StockMovementResponseDto.class)))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<Page<StockMovementResponseDto>> findAll(
            @Parameter(description = "Optional movement type filter: ENTRY or EXIT")
            @RequestHeader(value = "type", required = false)Type type,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable){
        Page<StockMovementResponseDto> movements = stockMovementService.findAll(type, pageable);
        return ResponseEntity.ok(movements);

    }

    @Operation(summary = "Retrieve a stock movement by code",
            description = "Retrieves a stock movement by its generated movement code. Accessible by admins and attendants.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stock movement retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StockMovementResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Stock movement not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/code/{movementCode}")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<StockMovementResponseDto> findByMovementCode(
            @Parameter(description = "Generated stock movement code", example = "202610-000001")
            @PathVariable String movementCode){
        StockMovementResponseDto movement = stockMovementService.findByMovementCode(movementCode);
        return ResponseEntity.ok(movement);
    }
}
