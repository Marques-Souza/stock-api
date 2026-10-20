package com.marquesdev.stock.repository;

import com.marquesdev.stock.entity.StockMovement;
import com.marquesdev.stock.enums.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID>, JpaSpecificationExecutor<StockMovement>{

    @Query(value = "SELECT nextval('stock_movement_code_seq')", nativeQuery = true)
    long nextMovementCode();

    Page<StockMovement> findAllByType(Type type, Pageable pageable);
    Optional<StockMovement> findByMovementCode(String movementCode);
}
