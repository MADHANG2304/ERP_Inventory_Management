package com.example.repository;

import com.example.entity.ReturnedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnedItemRepository
        extends JpaRepository<ReturnedItem, Long> {

}