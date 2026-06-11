package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.InventoryStock;

public interface InventoryStockRepository
        extends JpaRepository<InventoryStock, Long>,
        JpaSpecificationExecutor<InventoryStock> {

    @Query("""
    SELECT COUNT(i)
    FROM InventoryItem i
    WHERE
        (
            i.isReusable = false
            AND EXISTS (
                SELECT s
                FROM InventoryStock s
                WHERE s.item = i
                AND s.availableQuantity <= i.minimumStock
            )
        )
        OR
        (
            i.isReusable = true
            AND (
                SELECT COUNT(a)
                FROM AssetItem a
                WHERE a.item = i
            )   <= i.minimumStock
        )
    """)
    Long countLowStockItems();
    
}