package com.example.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dto.RequestTrackingDTO;
import com.example.entity.Employee;
import com.example.entity.InventoryRequest;
import com.example.entity.RequestApproval;
import com.example.repository.EmployeeRepository;
import com.example.repository.InventoryRequestRepository;
import com.example.repository.RequestApprovalRepository;

@Service
public class RequestTrackingService {

    private final InventoryRequestRepository inventoryRequestRepository;

    private final RequestApprovalRepository requestApprovalRepository;

    private final EmployeeRepository employeeRepository;

    public RequestTrackingService(
            InventoryRequestRepository inventoryRequestRepository,
            RequestApprovalRepository requestApprovalRepository,
            EmployeeRepository employeeRepository) {

        this.inventoryRequestRepository =
                inventoryRequestRepository;

        this.requestApprovalRepository =
                requestApprovalRepository;

        this.employeeRepository =
                employeeRepository;
    }

        public List<RequestTrackingDTO> getTrackingRequests(String username, String role) {

                Employee employee = employeeRepository.findByUsername(username);

                if(employee == null) {

                        throw new RuntimeException(
                                "Employee not found"
                        );
                }

                if("ROLE_SUPER_ADMIN".equals(role) || "ROLE_INVENTORY_ADMIN".equals(role)) {

                        return inventoryRequestRepository
                                .findAll()
                                .stream()
                                .sorted(
                                        Comparator.comparing(
                                                InventoryRequest::getRequestDate
                                        ).reversed()
                                )
                                .map(this::convertToDTO)
                                .toList();
                }

                if("ROLE_MANAGER".equals(role)) {

                        List<Long> requestIds =

                                requestApprovalRepository
                                        .findAll()
                                        .stream()

                                        .filter(approval ->

                                                approval.getApprover() != null

                                                &&

                                                approval.getApprover()
                                                        .getEmployeeId()
                                                        .equals(
                                                                employee.getEmployeeId()
                                                        )
                                        )

                                        .map(approval ->

                                                approval.getRequest()
                                                        .getRequestId()
                                        )

                                        .distinct()

                                        .toList();

                        return inventoryRequestRepository
                                .findAllById(requestIds)
                                .stream()

                                .sorted(
                                        Comparator.comparing(
                                                InventoryRequest::getRequestDate
                                        ).reversed()
                                )

                                .map(this::convertToDTO)

                                .toList();
                }

                List<Long> requestIds =

                        requestApprovalRepository
                                .findAll()
                                .stream()

                                .filter(approval ->

                                        approval.getApprover() != null

                                        &&

                                        approval.getApprover()
                                                .getEmployeeId()
                                                .equals(
                                                        employee.getEmployeeId()
                                                )
                                )

                                .map(approval ->

                                        approval.getRequest()
                                                .getRequestId()
                                )

                                .distinct()

                                .toList();

                return inventoryRequestRepository
                        .findAllById(requestIds)
                        .stream()

                        .sorted(
                                Comparator.comparing(
                                        InventoryRequest::getRequestDate
                                ).reversed()
                        )

                        .map(this::convertToDTO)

                        .toList();
        }

    private RequestTrackingDTO convertToDTO(
            InventoryRequest request) {

        RequestTrackingDTO dto =
                new RequestTrackingDTO();

        dto.setRequestId(
                request.getRequestId()
        );

        dto.setRequestNumber(
                request.getRequestNumber()
        );

        dto.setEmployeeId(
                request.getEmployee()
                        .getEmployeeId()
        );

        dto.setEmployeeName(
                request.getEmployee()
                        .getEmployeeName()
        );

        dto.setDepartmentName(
                request.getEmployee()
                        .getDepartment()
                        .getDepartmentName()
        );

        dto.setRequestStatus(
                request.getRequestStatus()
        );

        dto.setRemarks(
                request.getRemarks()
        );

        dto.setRequestDate(
                request.getRequestDate()
        );

        return dto;
    }
}