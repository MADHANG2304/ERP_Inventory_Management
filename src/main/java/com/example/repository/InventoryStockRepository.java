package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.InventoryStock;

public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long>, JpaSpecificationExecutor<InventoryStock>{
    @Query("""
    SELECT COUNT(s)
    FROM InventoryStock s
    WHERE s.availableQuantity <= s.item.minimumStock
    """)
    Long countLowStockItems();
}
