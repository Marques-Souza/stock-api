package com.marquesdev.stock.repository;

import com.marquesdev.stock.entity.Client;
import com.marquesdev.stock.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    boolean existsByEmail(String email);

    boolean existsByCnpj(String cnpj);


    @Query("""
                SELECT s
                FROM Supplier s
                WHERE LOWER(s.cnpj) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Supplier> findBySearch(@Param("search") String search, Pageable pageable);
}
