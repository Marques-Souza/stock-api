package com.marquesdev.stock.repository;

import com.marquesdev.stock.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    @Query("""
    SELECT p
    FROM Product p
    WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<Product> findBySearch(@Param("search") String search, Pageable pageable);
}
