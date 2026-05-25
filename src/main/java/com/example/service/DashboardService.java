package com.example.service;

import com.example.dto.DashboardStatsDTO;
import com.example.entity.Employee;
import com.example.entity.User;
import com.example.enums.ApprovalStatus;
import com.example.enums.ItemStatus;
import com.example.enums.RequestStatus;
import com.example.repository.EmployeeRepository;
import com.example.repository.InventoryItemRepository;
import com.example.repository.InventoryStockRepository;
import com.example.repository.InventoryRequestRepository;
import com.example.repository.RequestApprovalRepository;
import com.example.repository.UserRepository;
import com.example.security.SecurityService;

import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final InventoryRequestRepository requestRepository;

    private final RequestApprovalRepository approvalRepository;

    private final InventoryItemRepository itemRepository;

    private final InventoryStockRepository stockRepository;

    private final EmployeeRepository employeeRepository;

    private final UserRepository userRepository;

    private final SecurityService securityService;

    public DashboardService(
            InventoryRequestRepository requestRepository,
            RequestApprovalRepository approvalRepository,
            InventoryItemRepository itemRepository,
            InventoryStockRepository stockRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            SecurityService securityService
    ) {

        this.requestRepository = requestRepository;
        this.approvalRepository = approvalRepository;
        this.itemRepository = itemRepository;
        this.stockRepository = stockRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    public DashboardStatsDTO getDashboardStats() {

        DashboardStatsDTO dto = new DashboardStatsDTO();

        String role =
                securityService.getAuthenticatedRole();

        String username =
                securityService.getAuthenticatedUser();

        User user =
                userRepository.findByUsername(username);

        Employee employee =
                user.getEmployee();

        // EMPLOYEE
        if(role.equals("ROLE_EMPLOYEE")) {
                
            dto.setTotalRequests(
                    requestRepository.countByEmployee(employee)
            );

            dto.setDraftRequests(
                    requestRepository.countByEmployeeAndRequestStatus(
                            employee,
                            RequestStatus.DRAFT
                    )
            );

            dto.setPendingRequests(
                    requestRepository.countByEmployeeAndRequestStatus(
                            employee,
                            RequestStatus.PENDING_APPROVAL
                    )
            );

            dto.setApprovedRequests(
                    requestRepository.countByEmployeeAndRequestStatus(
                            employee,
                            RequestStatus.APPROVED
                    )
            );

            dto.setIssuedRequests(
                    requestRepository.countByEmployeeAndRequestStatus(
                            employee,
                            RequestStatus.ISSUED
                    )
            );
        }

        // MANAGER / INVENTORY ADMIN / SUPER ADMIN
        else {

            dto.setPendingApprovals(
                    approvalRepository.countByApprovalStatus(
                            ApprovalStatus.PENDING
                    )
            );

            dto.setApprovedRequests(
                    requestRepository.countByRequestStatus(
                            RequestStatus.APPROVED
                    )
            );

            dto.setIssuedRequests(
                    requestRepository.countByRequestStatus(
                            RequestStatus.ISSUED
                    )
            );

            dto.setPendingRequests(
                    requestRepository.countByRequestStatus(
                            RequestStatus.PENDING_APPROVAL
                    )
            );

            dto.setLowStockItems(
                    stockRepository.countLowStockItems()
            );

            dto.setOutOfStockItems(
                    itemRepository.countByStatus(
                            ItemStatus.OUT_OF_STOCK
                    )
            );
        }

        // SUPER ADMIN EXTRA
        if(role.equals("ROLE_SUPER_ADMIN")) {

            dto.setTotalRequests(
                    requestRepository.countByEmployee(employee)
            );

            dto.setTotalEmployees(
                    employeeRepository.count()
            );

            dto.setTotalItems(
                    itemRepository.count()
            );
        }

        return dto;
    }
}