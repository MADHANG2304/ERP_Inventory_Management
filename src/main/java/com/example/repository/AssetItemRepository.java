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

        

    @Query("""
        SELECT COUNT(a)
        FROM AssetItem a
        WHERE a.item.itemId = :itemId
        AND a.assetStatus = 'AVAILABLE'
    """)
    Long countAvailableAssets(Long itemId);

    Long countByItemItemIdAndAssetStatus(
            Long itemId,
            AssetStatus assetStatus
    );

    List<AssetItem> findByItem(InventoryItem item);

    boolean existsByAssetReferenceNumber(
            String assetReferenceNumber
    );

    long countByItemItemId(Long itemId);
}