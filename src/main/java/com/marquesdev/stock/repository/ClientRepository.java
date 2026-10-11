package com.marquesdev.stock.repository;

import com.marquesdev.stock.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface ClientRepository extends JpaRepository<Client, UUID> {
    boolean existsByEmail(String email);
    boolean existsByCnpj(String cnpj);


    @Query("""
    SELECT c
    FROM Client c
    WHERE LOWER(c.cnpj) LIKE LOWER(CONCAT('%', :search, '%'))
    OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
    OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    Page<Client> findBySearch(@Param("search") String search, Pageable pageable);

}
