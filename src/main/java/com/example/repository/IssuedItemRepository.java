package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
}