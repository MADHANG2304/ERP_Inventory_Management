package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.entity.AssetItem;
import com.example.entity.InventoryItem;
import com.example.enums.AssetStatus;

public interface AssetItemRepository
        extends JpaRepository<AssetItem, Long>,
        JpaSpecificationExecutor<AssetItem> {

    Long countByItemItemIdAndAssetStatus(
            Long itemId,
            AssetStatus assetStatus
    );

    long countByItemItemId(Long itemId);
}