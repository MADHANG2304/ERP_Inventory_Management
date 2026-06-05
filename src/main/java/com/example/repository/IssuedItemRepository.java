package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.IssuedItem;

@Repository
public interface IssuedItemRepository
        extends JpaRepository<IssuedItem, Long> {

        boolean existsByRequestItemRequestItemId(
                Long requestItemId
        );

        List<IssuedItem> findByRequestRequestId(
                Long requestId
        );

        List<IssuedItem> findByRequestItemRequestItemId(
                Long requestItemId
        );

        @Query("""
                SELECT COALESCE(SUM(i.issuedQuantity),0)
                FROM IssuedItem i
                WHERE i.requestItem.requestItemId = :requestItemId
                """)
        Integer getIssuedQuantityForRequestItem(
                Long requestItemId
        );

        List<IssuedItem> findByAssetItemAssetItemId(
                Long assetItemId
        );

        List<IssuedItem> findByAssetItemAssetItemIdOrderByIssuedDateDesc(
                Long assetItemId
        );
        
}