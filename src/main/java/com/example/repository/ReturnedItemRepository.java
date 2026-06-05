package com.example.repository;

import com.example.entity.ReturnedItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnedItemRepository
        extends JpaRepository<ReturnedItem, Long> {

        List<ReturnedItem> findByAssetItemAssetItemId(
            Long assetItemId
        );

        List<ReturnedItem> findByIssuedItemIssuedItemId(
                Long issuedItemId
        );

}