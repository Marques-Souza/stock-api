package com.marquesdev.stock.repository;

import com.marquesdev.stock.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByNameIgnoreCase(String name);


    @Query("""
        SELECT c
        FROM Category c
        WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
        """)
    Page<Category> findBySearch(@Param("search") String search, Pageable pageable);
}
