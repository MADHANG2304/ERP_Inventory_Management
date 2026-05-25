package com.example.repository;

import com.example.entity.RequestItems;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface RequestItemRepository
extends JpaRepository<RequestItems, Long>,  JpaSpecificationExecutor<RequestItems> {
        
        @Transactional
        @Modifying
        void deleteByRequest_RequestId(Long requestId);
}