package com.example.service;

import com.example.dto.DashboardStatsDTO;
import com.example.entity.Employee;
import com.example.enums.ApprovalStatus;
import com.example.enums.ItemStatus;
import com.example.enums.RequestStatus;
import com.example.repository.EmployeeRepository;
import com.example.repository.InventoryCategoryRepository;
import com.example.repository.InventoryItemRepository;
import com.example.repository.InventoryStockRepository;
import com.example.repository.InventoryRequestRepository;
import com.example.repository.RequestApprovalRepository;
import com.example.security.SecurityService;
import com.example.utils.NotificationUtil;

import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final InventoryRequestRepository requestRepository;

    private final RequestApprovalRepository approvalRepository;

    private final InventoryCategoryRepository categoryRepository;

    private final InventoryItemRepository itemRepository;

    private final InventoryStockRepository stockRepository;

    private final EmployeeRepository employeeRepository;

    private final ApprovalProcessService approvalProcessService;

    

    private final SecurityService securityService;

    public DashboardService(
            InventoryRequestRepository requestRepository,
            RequestApprovalRepository approvalRepository,
            InventoryItemRepository itemRepository,
            InventoryStockRepository stockRepository,
            EmployeeRepository employeeRepository,
            SecurityService securityService,
            ApprovalProcessService approvalProcessService,
            InventoryCategoryRepository categoryRepository
    ) {

        this.requestRepository = requestRepository;
        this.approvalRepository = approvalRepository;
        this.itemRepository = itemRepository;
        this.stockRepository = stockRepository;
        this.employeeRepository = employeeRepository;
        this.securityService = securityService;
        this.approvalProcessService = approvalProcessService;
        this.categoryRepository = categoryRepository;
    }

    public DashboardStatsDTO getDashboardStats() {

        DashboardStatsDTO dto = new DashboardStatsDTO();

        String role =
                securityService.getAuthenticatedRole();

        String username =
                securityService.getAuthenticatedUser();

        Employee employee =
                employeeRepository.findByUsername(username);


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

            dto.setPartiallyIssuedRequests(

                    requestRepository.countByEmployeeAndRequestStatus(
                            employee,
                            RequestStatus.PARTIALLY_ISSUED
                    )
            );

            dto.setRejected(

                    requestRepository.countByEmployeeAndRequestStatus(
                            employee,
                            RequestStatus.REJECTED
                    )
            );



        }

        // MANAGER / INVENTORY ADMIN / SUPER ADMIN
        else{

            dto.setPendingApprovals(
                (long) approvalProcessService.getPendingApprovals(username).size()

            );

            dto.setApprovedRequests(
                    requestRepository.countByRequestStatus(
                            RequestStatus.APPROVED
                    )
            );

            dto.setIssuedRequests(
                    requestRepository.countByEmployeeAndRequestStatus(
                            employee,
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

            dto.setTotalCategory(
                categoryRepository.count()
            );
        }

        // SUPER ADMIN EXTRA
        if(role.equals("ROLE_SUPER_ADMIN") || role.equals("ROLE_INVENTORY_ADMIN")) {

            dto.setTotalRequests(
                requestRepository.count()
            );

            dto.setTotalEmployees(
                    employeeRepository.count()
            );

            dto.setTotalItems(
                    itemRepository.count()
            );

            dto.setIssuedRequests(
                    requestRepository.countByRequestStatus(
                            RequestStatus.ISSUED
                    )
            );
        }

        else if(role.equals("ROLE_MANAGER")){
                dto.setIssuedRequests(
                    requestRepository.countByEmployeeAndRequestStatus(
                            employee,
                            RequestStatus.ISSUED
                    )
                );

                dto.setApprovedRequests(
                    requestRepository.countByEmployeeAndRequestStatus(
                            employee,
                            RequestStatus.APPROVED
                    )
                );

                dto.setPendingRequests(
                    requestRepository.countByEmployeeAndRequestStatus(
                        employee,
                        RequestStatus.PENDING_APPROVAL
                    )
                );

                dto.setPartiallyIssuedRequests(

                    requestRepository.countByEmployeeAndRequestStatus(
                            employee,
                            RequestStatus.PARTIALLY_ISSUED
                    )
                );

                dto.setRejected(

                        requestRepository.countByEmployeeAndRequestStatus(
                                employee,
                                RequestStatus.REJECTED
                        )
                );
                
        }

        return dto;
    }
}