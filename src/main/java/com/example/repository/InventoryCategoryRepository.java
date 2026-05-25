package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.entity.InventoryCategory;

public interface InventoryCategoryRepository extends JpaRepository<InventoryCategory,Long>, JpaSpecificationExecutor<InventoryCategory>{
    
}
