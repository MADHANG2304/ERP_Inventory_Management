package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.entity.RequestApproval;
import com.example.enums.ApprovalStatus;

@Repository
public interface RequestApprovalRepository extends
        JpaRepository<RequestApproval, Long>,
        JpaSpecificationExecutor<RequestApproval> {
        
        List<RequestApproval> findByRequest_RequestIdOrderByApprovalOrderAsc(Long requestId);

        void deleteByRequest_RequestId(Long requestId);

        Long countByApprovalStatus(ApprovalStatus status);
                
}