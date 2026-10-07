package com.marquesdev.stock.repository;

import com.marquesdev.stock.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    boolean existsByEmail(String email);
    boolean existsByCnpj(String cnpj);
}
