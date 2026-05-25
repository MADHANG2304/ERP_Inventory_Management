package com.example.repository;

import com.example.entity.Employee;
import com.example.entity.InventoryRequest;
import com.example.enums.RequestStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRequestRepository extends
        JpaRepository<InventoryRequest, Long>,
        JpaSpecificationExecutor<InventoryRequest> {

        Long countByRequestStatus(RequestStatus status);

        Long countByEmployee(Employee employee);

        Long countByEmployeeAndRequestStatus(
                Employee employee,
                RequestStatus status
        );
}