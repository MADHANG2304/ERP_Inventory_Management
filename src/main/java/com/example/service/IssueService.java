package com.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dto.IssuedItemDTO;
import com.example.entity.AssetItem;
import com.example.entity.Employee;
import com.example.entity.InventoryRequest;
import com.example.entity.InventoryStock;
import com.example.entity.InventoryTransaction;
import com.example.entity.IssuedItem;
import com.example.entity.RequestItems;
import com.example.entity.Employee;
import com.example.enums.AssetStatus;
import com.example.enums.IssueStatus;
import com.example.enums.ReferenceType;
import com.example.enums.RequestStatus;
import com.example.enums.TransactionType;
import com.example.repository.InventoryRequestRepository;
import com.example.repository.InventoryStockRepository;
import com.example.repository.InventoryTransactionRepository;
import com.example.repository.IssuedItemRepository;
import com.example.repository.RequestItemRepository;
import com.example.repository.AssetItemRepository;
import com.example.repository.EmployeeRepository;
import com.example.security.SecurityService;

@Service
public class IssueService {

        private final InventoryRequestRepository inventoryRequestRepository;

        private final InventoryStockRepository inventoryStockRepository;

        private final InventoryTransactionRepository inventoryTransactionRepository;

        private final IssuedItemRepository issuedItemRepository;

        private final RequestItemRepository requestItemsRepository;

        private final EmployeeRepository employeeRepository;

        private final SecurityService securityService;

        private final AuditLogService auditLogService;

        private final AssetItemRepository assetItemRepository;

        public IssueService(
                        InventoryRequestRepository inventoryRequestRepository,
                        InventoryStockRepository inventoryStockRepository,
                        InventoryTransactionRepository inventoryTransactionRepository,
                        IssuedItemRepository issuedItemRepository,
                        RequestItemRepository requestItemsRepository,
                        EmployeeRepository employeeRepository,
                        AuditLogService auditLogService,
                        SecurityService securityService,
                        AssetItemRepository assetItemRepository) {

                this.inventoryRequestRepository = inventoryRequestRepository;

                this.inventoryStockRepository = inventoryStockRepository;

                this.inventoryTransactionRepository = inventoryTransactionRepository;

                this.issuedItemRepository = issuedItemRepository;

                this.requestItemsRepository = requestItemsRepository;

                this.employeeRepository = employeeRepository;

                this.auditLogService = auditLogService;

                this.securityService = securityService;

                this.assetItemRepository = assetItemRepository;

        }

        public List<IssuedItemDTO> getApprovedRequests() {

        return inventoryRequestRepository
                .findAll()
                .stream()

                .filter(request ->
                        request.getRequestStatus() == RequestStatus.APPROVED
                        || request.getRequestStatus() == RequestStatus.ISSUED)

                .flatMap(request ->

                        request.getRequestItems()
                                .stream()

                                .filter(item ->

                                        !issuedItemRepository
                                                .existsByRequestItemRequestItemId(
                                                        item.getRequestItemId()
                                                )
                                )

                                .map(item -> {

                                        IssuedItemDTO dto =
                                                new IssuedItemDTO();

                                        dto.setRequestId(
                                                request.getRequestId()
                                        );

                                        dto.setRequestItemId(
                                                item.getRequestItemId()
                                        );

                                        dto.setRequestNumber(
                                                request.getRequestNumber()
                                        );

                                        dto.setEmployeeName(
                                                request.getEmployee().getEmployeeName()
                                        );

                                        dto.setItemName(
                                                item.getItem().getItemName()
                                        );

                                        dto.setItemCode(
                                                item.getItem().getItemCode()
                                        );

                                        dto.setRequestedQuantity(
                                                item.getRequestedQuantity()
                                        );

                                        // dto.setIssuedQuantity(
                                        //         item.getRequestedQuantity()
                                        // );

                                        dto.setIssuedQuantity(
                                                item.getRequestedQuantity()
                                        );

                                        if(Boolean.TRUE.equals(
                                                item.getItem().getIsReusable()
                                        )) {
                                                AssetItem asset =
                                                        assetItemRepository.findAll()
                                                                .stream()
                                                                .filter(a ->

                                                                        a.getItem()
                                                                                .getItemId()
                                                                                .equals(
                                                                                        item.getItem().getItemId()
                                                                                )

                                                                        && a.getAssetStatus()
                                                                                == AssetStatus.AVAILABLE
                                                                )
                                                                .findFirst()
                                                                .orElse(null);

                                                if(asset != null) {

                                                        dto.setAssetReferenceNumber(
                                                                asset.getAssetReferenceNumber()
                                                        );
                                                }
                                        }

                                        return dto;
                                })
                )

                .toList();
        }

        public void issueItem(Long requestItemId, String username) {

                RequestItems requestItem = requestItemsRepository
                        .findById(requestItemId)
                        .orElseThrow(() ->
                                new RuntimeException("Request item not found"));

                InventoryRequest request =
                        requestItem.getRequest();

                Employee issuedBy =
                        employeeRepository.findAll()
                                .stream()
                                .filter(user ->
                                        user.getUsername().equals(username))
                                .findFirst()
                                .orElseThrow(() ->
                                        new RuntimeException("User not found"));

                boolean reusable =
                        Boolean.TRUE.equals(
                                requestItem.getItem().getIsReusable()
                        );

                IssuedItem issuedItem =
                        new IssuedItem();

                issuedItem.setRequest(request);

                issuedItem.setRequestItem(requestItem);

                issuedItem.setIssuedToEmployee(
                        request.getEmployee()
                );

                issuedItem.setIssuedBy(
                        issuedBy
                );

                issuedItem.setIssuedDate(
                        LocalDateTime.now()
                );

                issuedItem.setIssueReferenceNumber(
                        generateReferenceNumber()
                );

                if(reusable) {

                        AssetItem asset =
                                assetItemRepository.findAll()
                                        .stream()
                                        .filter(a ->

                                                a.getItem()
                                                        .getItemId()
                                                        .equals(
                                                                requestItem.getItem().getItemId()
                                                        )

                                                && a.getAssetStatus()
                                                        == AssetStatus.AVAILABLE
                                        )
                                        .findFirst()
                                        .orElseThrow(() ->
                                                new RuntimeException(
                                                        "No available asset found"
                                                )
                                        );

                        asset.setAssetStatus(
                                AssetStatus.ISSUED
                        );

                        assetItemRepository.save(asset);

                        issuedItem.setAssetItem(asset);

                        issuedItem.setIssuedQuantity(1);

                } else {

                        InventoryStock stock =
                                inventoryStockRepository.findAll()
                                        .stream()
                                        .filter(s ->

                                                s.getItem()
                                                        .getItemId()
                                                        .equals(
                                                                requestItem.getItem().getItemId()
                                                        )
                                        )
                                        .findFirst()
                                        .orElseThrow(() ->
                                                new RuntimeException(
                                                        "Stock not found"
                                                )
                                        );

                        if(stock.getAvailableQuantity()
                                < requestItem.getRequestedQuantity()) {

                        throw new RuntimeException(
                                "Insufficient stock"
                        );
                        }

                        stock.setAvailableQuantity(
                                stock.getAvailableQuantity()
                                        - requestItem.getRequestedQuantity()
                        );

                        stock.setIssuedQuantity(
                                stock.getIssuedQuantity()
                                        + requestItem.getRequestedQuantity()
                        );

                        inventoryStockRepository.save(stock);

                        issuedItem.setIssuedQuantity(
                                requestItem.getRequestedQuantity()
                        );
                }

                issuedItem.setIssueStatus(
                        IssueStatus.ISSUED
                );

                issuedItemRepository.save(issuedItem);

                InventoryTransaction transaction =
                        new InventoryTransaction();

                transaction.setItem(
                        requestItem.getItem()
                );

                transaction.setTransactionType(
                        TransactionType.ISSUE
                );

                transaction.setReferenceType(
                        ReferenceType.REQUEST
                );

                transaction.setReferenceNumber(
                        issuedItem.getIssueReferenceNumber()
                );

                transaction.setQuantity(
                        issuedItem.getIssuedQuantity()
                );

                transaction.setTransactionDate(
                        LocalDateTime.now()
                );

                transaction.setRemarks(
                        "Item issued"
                );

                inventoryTransactionRepository.save(
                        transaction
                );

                request.setRequestStatus(
                        RequestStatus.ISSUED
                );

                inventoryRequestRepository.save(
                        request
                );

                auditLogService.logAction(
                        "ISSUE_MODULE",
                        "ISSUE",
                        "Issued item : "
                                + requestItem.getItem().getItemName()
                );
        }

        public List<IssuedItemDTO> getIssuedHistory() {

                String username = securityService.getAuthenticatedUser();

                String role = securityService.getAuthenticatedRole();

                Employee loggedInUser = employeeRepository
                                .findAll()
                                .stream()
                                .filter(user -> user.getUsername().equals(username))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return issuedItemRepository
                                .findAll()
                                .stream()

                                .filter(item -> item.getIssueStatus() != IssueStatus.RETURNED)

                                .filter(item -> {

                                        if (role.equals("ROLE_SUPER_ADMIN") || role.equals("ROLE_INVENTORY_ADMIN")) {
                                                return true;
                                        }

                                        return item.getIssuedToEmployee() != null
                                                        && loggedInUser != null
                                                        && item.getIssuedToEmployee().getEmployeeId()
                                                                        .equals(loggedInUser.getEmployeeId());
                                })

                                .map(item -> {

                                        IssuedItemDTO dto = new IssuedItemDTO();

                                        // dto.setAssetReferenceNumber(item.getIssueReferenceNumber());

                                        dto.setEmployeeName(item.getIssuedToEmployee().getEmployeeName());

                                        dto.setItemName(item.getRequestItem().getItem().getItemName());

                                        dto.setItemCode(item.getRequestItem().getItem().getItemCode());

                                        if(Boolean.TRUE.equals(
                                                item.getRequestItem().getItem().getIsReusable())) {

                                                AssetItem asset =
                                                        assetItemRepository.findAll()
                                                                .stream()
                                                                .filter(a ->

                                                                        a.getItem()
                                                                                .getItemId()
                                                                                .equals(
                                                                                        item.getRequestItem().getItem().getItemId()
                                                                                )

                                                                        && a.getAssetStatus()
                                                                                == AssetStatus.AVAILABLE
                                                                )
                                                                .findFirst()
                                                                .orElse(null);

                                                if(asset != null) {

                                                        dto.setAssetReferenceNumber(
                                                                asset.getAssetReferenceNumber()
                                                        );
                                                }
                                        }

                                        dto.setIssuedQuantity(item.getIssuedQuantity());

                                        if(item.getAssetItem() != null) {

                                                dto.setAssetReferenceNumber(
                                                        item.getAssetItem()
                                                                .getAssetReferenceNumber()
                                                );
                                        }

                                        return dto;
                                })
                                .toList();
        }

        

        private String generateReferenceNumber() {
                return "ISSUE-" + UUID.randomUUID().toString().substring(0, 8);
        }
}