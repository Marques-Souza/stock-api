package com.marquesdev.stock.service;

import com.marquesdev.stock.dto.StockMovementRequestDto;
import com.marquesdev.stock.dto.StockMovementResponseDto;
import com.marquesdev.stock.entity.Client;
import com.marquesdev.stock.entity.Product;
import com.marquesdev.stock.entity.StockMovement;
import com.marquesdev.stock.entity.Supplier;
import com.marquesdev.stock.enums.Type;
import com.marquesdev.stock.exception.EntityNotFoundException;
import com.marquesdev.stock.exception.InvalidStockMovementException;
import com.marquesdev.stock.mapper.StockMovementMapper;
import com.marquesdev.stock.repository.ClientRepository;
import com.marquesdev.stock.repository.ProductRepository;
import com.marquesdev.stock.repository.StockMovementRepository;
import com.marquesdev.stock.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final StockMovementMapper stockMovementMapper;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final SupplierRepository supplierRepository;

    @Transactional
    public StockMovementResponseDto createMovement(StockMovementRequestDto dto) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Supplier supplier = null;
        Client client = null;

        if (dto.type() == Type.ENTRY) {
            if (dto.supplierId() == null || dto.clientId() != null) {
                throw new InvalidStockMovementException("An entry requires a supplier.");
            }
            supplier = supplierRepository.findById(dto.supplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
            product.addQuantity(dto.quantity());

        } else if (dto.type() == Type.EXIT) {
            if (dto.clientId() == null || dto.supplierId() != null) {
                throw new InvalidStockMovementException("An exit requires a client.");
            }
            client = clientRepository.findById(dto.clientId())
                    .orElseThrow(() -> new EntityNotFoundException("Client not found"));
            product.removeQuantity(dto.quantity());
        }

        StockMovement movement = stockMovementMapper.toEntity(dto, product, supplier, client);
        movement.setMovementCode(generateMovementCode());
        StockMovement savedMovement = stockMovementRepository.save(movement);
        return stockMovementMapper.toDto(savedMovement);


    }

    @Transactional(readOnly = true)
    public Page<StockMovementResponseDto> findAll(Type type, Pageable pageable) {
        Page<StockMovement> movementsPage = stockMovementRepository.findAll(pageable);

        if (type != null){
            movementsPage = stockMovementRepository.findAllByType(type, pageable);
        }
        return movementsPage.map(stockMovementMapper::toDto);
    }


    @Transactional(readOnly = true)
    public StockMovementResponseDto findByMovementCode(String movementCode) {
        StockMovement movement = stockMovementRepository.findByMovementCode(movementCode)
                .orElseThrow(() -> new EntityNotFoundException("Stock movement not found"));
        return stockMovementMapper.toDto(movement);
    }



    private String generateMovementCode(){
        long number = stockMovementRepository.nextMovementCode();
        String month = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        return month + "-" + String.format("%06d", number);
    }
}
