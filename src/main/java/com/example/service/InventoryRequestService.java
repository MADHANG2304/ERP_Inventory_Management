package com.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.dto.InventoryItemDTO;
import com.example.dto.InventoryRequestDTO;
import com.example.dto.InventoryRequestFilterDTO;
import com.example.dto.RequestItemDTO;
import com.example.entity.ApprovalConfig;
import com.example.entity.ApprovalConfigLevel;
import com.example.entity.Employee;
import com.example.entity.InventoryItem;
import com.example.entity.InventoryRequest;
import com.example.entity.InventoryStock;
import com.example.entity.RequestApproval;
import com.example.entity.RequestItems;
import com.example.entity.Employee;
import com.example.enums.ApprovalStatus;
import com.example.enums.ItemStatus;
import com.example.enums.RequestStatus;
import com.example.enums.RequestType;
import com.example.repository.ApprovalConfigRepository;
import com.example.repository.AssetItemRepository;
import com.example.repository.EmployeeRepository;
import com.example.repository.InventoryItemRepository;
import com.example.repository.InventoryRequestRepository;
import com.example.repository.InventoryStockRepository;
import com.example.repository.RequestApprovalRepository;
import com.example.repository.RequestItemRepository;
import com.example.repository.EmployeeRepository;
import com.example.security.SecurityService;
import com.example.specification.InventoryRequestSpecification;

@Service
public class InventoryRequestService {

        private InventoryRequestRepository inventoryRequestRepository;

        private final RequestItemRepository requestItemRepository;

        private final InventoryItemRepository inventoryItemRepository;

        private final InventoryStockRepository inventoryStockRepository;

        private final ApprovalConfigRepository approvalConfigRepository;

        private final RequestApprovalRepository requestApprovalRepository;

        private final SecurityService securityService;

        private final EmployeeRepository employeeRepository;

        private final AuditLogService auditLogService;

        private final AssetItemRepository assetItemRepository;

        public InventoryRequestService(
                        InventoryRequestRepository inventoryRequestRepository,
                        RequestItemRepository requestItemRepository,
                        EmployeeRepository employeeRepository,
                        InventoryItemRepository inventoryItemRepository,
                        InventoryStockRepository inventoryStockRepository,
                        ApprovalConfigRepository approvalConfigRepository,
                        RequestApprovalRepository requestApprovalRepository,
                        SecurityService securityService,
                        AuditLogService auditLogService,
                        AssetItemRepository assetItemRepository) {

                this.inventoryRequestRepository = inventoryRequestRepository;

                this.requestItemRepository = requestItemRepository;

                this.employeeRepository = employeeRepository;

                this.inventoryItemRepository = inventoryItemRepository;

                this.inventoryStockRepository = inventoryStockRepository;

                this.approvalConfigRepository = approvalConfigRepository;

                this.requestApprovalRepository = requestApprovalRepository;

                this.securityService = securityService;

                this.auditLogService = auditLogService;

                this.assetItemRepository = assetItemRepository;
        }

        public InventoryRequestDTO saveDraft(InventoryRequestDTO dto) {

                auditLogService.logAction(

                        "REQUEST_MODULE",

                        "CREATE",

                        "Draft request created : "
                                + dto.getRequestNumber()
                );
                return saveRequest(dto , RequestStatus.DRAFT);
        }

        public InventoryRequestDTO submitRequest(InventoryRequestDTO dto) {

                InventoryRequestDTO savedRequest = saveRequest(dto , RequestStatus.PENDING_APPROVAL);

                generateApprovalWorkflow(savedRequest.getRequestId());

                auditLogService.logAction(

                        "REQUEST_MODULE",

                        "SUBMIT",

                        "Submitted request : "
                                + dto.getRequestNumber()
                );

                return convertToDTO(inventoryRequestRepository.findById(savedRequest.getRequestId()).orElseThrow());
        }

        private void generateApprovalWorkflow(Long requestId) {

                InventoryRequest request =
                        inventoryRequestRepository
                                .findById(requestId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Request not found"
                                        )
                                );

                RequestType highestRequestType =
                        determineHighestRequestType(request);

                String employeeRole =
                        request.getEmployee()
                                .getRole()
                                .getRoleName();

                com.example.enums.RequesterRole requesterRole;

                switch (employeeRole) {

                        case "EMPLOYEE" ->

                                requesterRole =
                                        com.example.enums.RequesterRole.EMPLOYEE;

                        case "MANAGER" ->

                                requesterRole =
                                        com.example.enums.RequesterRole.MANAGER;

                        case "INVENTORY_ADMIN" ->

                                requesterRole =
                                        com.example.enums.RequesterRole.INVENTORY_ADMIN;

                        default ->

                                throw new RuntimeException(
                                        "Requester role not supported : "
                                                + employeeRole
                                );
                }

                ApprovalConfig config =
                        approvalConfigRepository
                                .findAll()
                                .stream()

                                .filter(c ->

                                        Boolean.TRUE.equals(
                                                c.getIsActive()
                                        )

                                        &&

                                        c.getRequestType()
                                                == highestRequestType

                                        &&

                                        c.getRequesterRole()
                                                == requesterRole
                                )

                                .findFirst()

                                .orElseThrow(() ->

                                        new RuntimeException(

                                                "Approval configuration not found for "

                                                        + requesterRole

                                                        + " - "

                                                        + highestRequestType
                                        )
                                );

                request.getApprovals().clear();

                List<ApprovalConfigLevel> levels =
                        config.getLevels()
                                .stream()

                                .sorted(
                                        (a, b) ->

                                                a.getApprovalOrder()
                                                        .compareTo(
                                                                b.getApprovalOrder()
                                                        )
                                )

                                .toList();

                for (int i = 0; i < levels.size(); i++) {

                        ApprovalConfigLevel level =
                                levels.get(i);

                        RequestApproval approval =
                                new RequestApproval();

                        approval.setRequest(request);

                        approval.setApprovalOrder(
                                level.getApprovalOrder()
                        );

                        approval.setApprovalRole(
                                level.getApprovalRole()
                        );

                        approval.setApprovalStatus(
                                ApprovalStatus.PENDING
                        );

                        approval.setIsCurrentLevel(
                                i == 0
                        );

                        Employee approver = null;

                        if(level.getApprovalRole().name().equals("MANAGER")) {

                        approver =
                                employeeRepository
                                        .findManagerByDepartmentAndRole(

                                                request.getEmployee()
                                                        .getDepartment()
                                                        .getDepartmentId(),

                                                "MANAGER"
                                        );

                        if(approver == null) {

                                throw new RuntimeException(
                                        "Manager not found for department"
                                );
                        }
                        }

                        else if(level.getApprovalRole().name().equals("INVENTORY_ADMIN")) {

                        approver =
                                employeeRepository
                                        .findByUserRoleRoleName(
                                                "INVENTORY_ADMIN"
                                        );

                        if(approver == null) {

                                throw new RuntimeException(
                                        "Inventory Admin not found"
                                );
                        }
                        }

                        else if(level.getApprovalRole().name().equals("SUPER_ADMIN")) {

                        approver =
                                employeeRepository
                                        .findByUserRoleRoleName(
                                                "SUPER_ADMIN"
                                        );

                        if(approver == null) {

                                throw new RuntimeException(
                                        "Super Admin not found"
                                );
                        }
                        }

                        approval.setApprover(approver);

                        request.getApprovals().add(approval);
                }

                inventoryRequestRepository.save(request);
        }

        private RequestType determineHighestRequestType(InventoryRequest request) {

                boolean hasBulk = request.getRequestItems()
                                .stream()
                                .anyMatch(item -> item.getRequestedQuantity() >= 10);

                if (hasBulk) {
                        return RequestType.BULK_REQUEST;
                }

                boolean hasHighValue = request.getRequestItems()
                                .stream()
                                .anyMatch(item ->
                                        item.getItem()
                                                .getApprovalType()
                                                .name()
                                                .equals("HIGH_VALUE"));

                if (hasHighValue) {
                        return RequestType.HIGH_VALUE;
                }

                return RequestType.LOW_VALUE;
        }

        public InventoryRequestDTO saveRequest(InventoryRequestDTO dto, RequestStatus status) {

                validateRequest(dto);

                InventoryRequest request;

                if (dto.getRequestId() != null) {

                        request = inventoryRequestRepository
                                        .findById(dto.getRequestId())
                                        .orElse(new InventoryRequest());

                } else {

                        request = new InventoryRequest();

                        request.setRequestNumber(dto.getRequestNumber());

                        request.setRequestDate(LocalDateTime.now());
                }

                Employee employee = employeeRepository
                                .findById(dto.getEmployeeId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Employee not found"));

                request.setEmployee(employee);

                request.setRemarks(dto.getRemarks());

                request.setRequestStatus(status);

                InventoryRequest savedRequest = inventoryRequestRepository.save(request);

                if(dto.getRequestId() != null) {

                        savedRequest.getRequestItems().clear();

                        inventoryRequestRepository.save(savedRequest);

                        inventoryRequestRepository.flush();
                }

                for (RequestItemDTO itemDTO : dto.getRequestItems()) {

                        InventoryItem item = inventoryItemRepository
                                        .findById(itemDTO.getItemId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Item not found"));

                        RequestItems requestItem = new RequestItems();

                        requestItem.setRequest(savedRequest);

                        requestItem.setItem(item);

                        requestItem.setRequestedQuantity(itemDTO.getRequestedQuantity());

                        requestItem.setApprovedQuantity(0);

                        requestItemRepository.save(requestItem);

                        // savedRequest = inventoryRequestRepository.save(savedRequest);
                }

                return convertToDTO(savedRequest);
        }

        public List<InventoryRequestDTO> getAllRequests() {

                String username =
                        securityService.getAuthenticatedUser();

                String role =
                        securityService.getAuthenticatedRole();

                Employee loggedInUser =
                        employeeRepository
                                .findAll()
                                .stream()
                                .filter(user -> user.getUsername().equals(username))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return inventoryRequestRepository
                        .findAll()
                        .stream()
                        .filter(request -> {

                                if(role.equals("ROLE_SUPER_ADMIN") || role.equals("ROLE_INVENTORY_ADMIN")) {
                                        return true;
                                }

                                return request.getEmployee() != null
                                        && loggedInUser != null
                                        && request.getEmployee().getEmployeeId()
                                                .equals(
                                                        loggedInUser.getEmployeeId()
                                                );
                        })

                        .sorted(
                                (r1, r2) ->

                                        r2.getRequestDate()
                                                .compareTo(
                                                        r1.getRequestDate()
                                                )
                        )

                        .map(this::convertToDTO)

                        .collect(Collectors.toList());
        }

        public List<InventoryRequestDTO> searchRequests(
                        String keyword) {

                Specification<InventoryRequest> specification = InventoryRequestSpecification
                                .searchRequest(keyword);

                return inventoryRequestRepository
                                .findAll(specification)
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
        }

        public List<InventoryItemDTO> getAvailableItems() {

                return inventoryItemRepository
                                .findAll()
                                .stream()
                                .filter(item -> item.getStatus() == ItemStatus.AVAILABLE)
                                .map(item -> {

                                        InventoryItemDTO dto = new InventoryItemDTO();

                                        dto.setItemId(
                                                        item.getItemId());

                                        dto.setItemName(
                                                        item.getItemName());

                                        dto.setItemCode(
                                                        item.getItemCode());

                                        dto.setUnitType(item.getUnitType());

                                        return dto;
                                })
                                .collect(Collectors.toList());
        }

        public InventoryRequestDTO getRequestById(Long requestId) {

                InventoryRequest request =
                        inventoryRequestRepository
                                .findById(requestId)
                                .orElseThrow(() -> new RuntimeException("Request not found"));

                return convertToDTO(request);
        }

        public List<InventoryRequestDTO> filterRequests(InventoryRequestFilterDTO filterDTO) {

                Specification<InventoryRequest>
                        specification = InventoryRequestSpecification
                                
                                .hasRequestNumber(filterDTO.getRequestNumber())

                                .and(InventoryRequestSpecification.hasEmployeeName(filterDTO.getEmployeeName()))

                                .and(InventoryRequestSpecification.hasStatus(filterDTO.getRequestStatus()))

                                .and(InventoryRequestSpecification.hasFromDate(filterDTO.getFromDate()))

                                .and(InventoryRequestSpecification.hasToDate(filterDTO.getToDate()));


                String role = securityService.getAuthenticatedRole();

                String username = securityService.getAuthenticatedUser();


                if(role.equals("ROLE_EMPLOYEE")) {

                        Employee employee = employeeRepository.findByUsername(username);

                        if(employee != null && employee != null) {

                        specification =
                                specification.and(
                                        InventoryRequestSpecification.hasEmployeeId(employee.getEmployeeId())
                                );
                        }
                }

                return inventoryRequestRepository
                        .findAll(specification)
                        .stream()
                        .map(this::convertToDTO)
                        .toList();
        }

        private void validateRequest(InventoryRequestDTO dto) {

                if (dto.getEmployeeId() == null) {

                        throw new RuntimeException(
                                "Employee required"
                        );
                }

                if (dto.getRequestItems() == null
                                || dto.getRequestItems().isEmpty()) {

                        throw new RuntimeException(
                                "At least one item required"
                        );
                }

                for (RequestItemDTO itemDTO : dto.getRequestItems()) {

                        if (itemDTO.getRequestedQuantity() == null
                                        || itemDTO.getRequestedQuantity() <= 0) {

                                throw new RuntimeException(
                                        "Invalid quantity"
                                );
                        }

                        InventoryItem item =
                                inventoryItemRepository
                                        .findById(itemDTO.getItemId())
                                        .orElseThrow(() ->
                                                new RuntimeException(
                                                        "Item not found"
                                                )
                                        );

                        // REUSABLE ITEM

                        if(Boolean.TRUE.equals(
                                item.getIsReusable()
                        )) {

                                Long availableAssets =
                                        assetItemRepository
                                                .countAvailableAssets(
                                                        item.getItemId()
                                                );

                                if(itemDTO.getRequestedQuantity()
                                        > availableAssets) {

                                        throw new RuntimeException(

                                                "Only "
                                                        + availableAssets
                                                        + " asset(s) available for "
                                                        + item.getItemName()
                                        );
                                }
                        }

                        // NON REUSABLE ITEM

                        else {

                                InventoryStock stock =
                                        inventoryStockRepository
                                                .findAll()
                                                .stream()

                                                .filter(s ->

                                                        s.getItem()
                                                                .getItemId()
                                                                .equals(
                                                                        itemDTO.getItemId()
                                                                )
                                                )

                                                .findFirst()

                                                .orElseThrow(() ->

                                                        new RuntimeException(
                                                                "Stock not found"
                                                        )
                                                );

                                if(itemDTO.getRequestedQuantity()
                                        > stock.getAvailableQuantity()) {

                                        throw new RuntimeException(

                                                "Insufficient stock for "
                                                        + stock.getItem()
                                                                .getItemName()
                                        );
                                }
                        }
                }
        }


        private InventoryRequestDTO convertToDTO(InventoryRequest request) {

                InventoryRequestDTO dto = new InventoryRequestDTO();

                dto.setRequestId(request.getRequestId());

                dto.setEmployeeId(request.getEmployee().getEmployeeId());

                dto.setEmployeeName(request.getEmployee().getEmployeeName());

                dto.setRequestNumber(request.getRequestNumber());

                dto.setRequestStatus(request.getRequestStatus());

                dto.setRemarks(request.getRemarks());

                dto.setRequestDate(request.getRequestDate());

                dto.setRequestItems(

                                request.getRequestItems()
                                                .stream()
                                                .map(item -> {

                                                        RequestItemDTO itemDTO = new RequestItemDTO();

                                                        itemDTO.setRequestItemId(item.getRequestItemId());

                                                        itemDTO.setItemId(item.getItem().getItemId());

                                                        itemDTO.setItemName(item.getItem().getItemName());

                                                        itemDTO.setItemCode(item.getItem().getItemCode());

                                                        itemDTO.setRequestedQuantity(item.getRequestedQuantity());

                                                        itemDTO.setApprovedQuantity(item.getApprovedQuantity());

                                                        return itemDTO;
                                                })
                                                .collect(Collectors.toList()));

                return dto;
        }
}