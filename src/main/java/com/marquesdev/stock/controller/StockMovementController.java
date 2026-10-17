package com.marquesdev.stock.controller;

import com.marquesdev.stock.dto.StockMovementRequestDto;
import com.marquesdev.stock.dto.StockMovementResponseDto;
import com.marquesdev.stock.enums.Type;
import com.marquesdev.stock.service.StockMovementService;
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
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<StockMovementResponseDto> createStockMovement(@Valid @RequestBody StockMovementRequestDto stockMovementRequestDto){
        StockMovementResponseDto movement = stockMovementService.createMovement(stockMovementRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(movement);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<Page<StockMovementResponseDto>> findAll(
            @RequestHeader(value = "type", required = false)Type type,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable){
        Page<StockMovementResponseDto> movements = stockMovementService.findAll(type, pageable);
        return ResponseEntity.ok(movements);

    }

    @GetMapping("/code/{movementCode}")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('ATTENDANT')")
    public ResponseEntity<StockMovementResponseDto> findByMovementCode(
            @PathVariable String movementCode){
        StockMovementResponseDto movement = stockMovementService.findByMovementCode(movementCode);
        return ResponseEntity.ok(movement);
    }
}
