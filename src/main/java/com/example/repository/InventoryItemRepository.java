package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.InventoryItem;
import com.example.enums.ItemStatus;

public interface InventoryItemRepository extends JpaRepository<InventoryItem,Long>, JpaSpecificationExecutor<InventoryItem>{


    Long countByStatus(ItemStatus status);
}
