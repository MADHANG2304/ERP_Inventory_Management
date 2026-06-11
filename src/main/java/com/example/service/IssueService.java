package com.example.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.dto.IssuedItemDTO;
import com.example.entity.AssetItem;
import com.example.entity.Employee;
import com.example.entity.InventoryRequest;
import com.example.entity.InventoryStock;
import com.example.entity.InventoryTransaction;
import com.example.entity.IssuedItem;
import com.example.entity.RequestItems;
import com.example.enums.AssetStatus;
import com.example.enums.IssueStatus;
import com.example.enums.ReferenceType;
import com.example.enums.RequestStatus;
import com.example.enums.TransactionType;
import com.example.repository.AssetItemRepository;
import com.example.repository.EmployeeRepository;
import com.example.repository.InventoryRequestRepository;
import com.example.repository.InventoryStockRepository;
import com.example.repository.InventoryTransactionRepository;
import com.example.repository.IssuedItemRepository;
import com.example.repository.RequestItemRepository;
import com.example.security.SecurityService;

import jakarta.transaction.Transactional;

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

    List<IssuedItemDTO> result = new ArrayList<>();

    List<InventoryRequest> requests =
            inventoryRequestRepository.findAll()
                    .stream()
                    .filter(request ->

                            request.getRequestStatus() == RequestStatus.APPROVED

                            ||

                            request.getRequestStatus() == RequestStatus.PARTIALLY_ISSUED
                    )
                    .toList();

        for (InventoryRequest request : requests) {

                for (RequestItems requestItem : request.getRequestItems()) {

                if (requestItem.getApprovedQuantity() == null
                        || requestItem.getApprovedQuantity() <= 0) {

                        continue;
                }

                Integer alreadyIssued =
                        issuedItemRepository.getIssuedQuantityForRequestItem(
                                requestItem.getRequestItemId()
                        );

                alreadyIssued =
                        alreadyIssued == null
                                ? 0
                                : alreadyIssued;

                if (alreadyIssued >= requestItem.getApprovedQuantity()) {

                        continue;
                }

                IssuedItemDTO dto = new IssuedItemDTO();

                dto.setRequestId(
                        request.getRequestId()
                );

                dto.setRequestNumber(
                        request.getRequestNumber()
                );

                dto.setRequestItemId(
                        requestItem.getRequestItemId()
                );

                dto.setEmployeeName(
                        request.getEmployee().getEmployeeName()
                );

                dto.setItemId(
                        requestItem.getItem().getItemId()
                );

                dto.setItemName(
                        requestItem.getItem().getItemName()
                );

                dto.setItemCode(
                        requestItem.getItem().getItemCode()
                );

                dto.setReusable(
                        requestItem.getItem().getIsReusable()
                );

                dto.setRequestedQuantity(
                        requestItem.getApprovedQuantity()
                );

                dto.setIssuedQuantity(
                        alreadyIssued
                );

                result.add(dto);
                }
        }

        return result;
        }

        @Transactional
        public void issueItem(Long requestItemId,Long assetItemId, Integer issueQuantity, String username) {

                RequestItems requestItem = requestItemsRepository
                        .findById(requestItemId)
                        .orElseThrow(() -> new RuntimeException("Request item not found"));

                InventoryRequest request = requestItem.getRequest();

                Employee issuedBy = employeeRepository.findAll()
                                .stream()
                                .filter(user -> user.getUsername().equals(username))
                                .findFirst()
                                .orElseThrow(() ->
                                        new RuntimeException("User not found"));

                boolean reusable = Boolean.TRUE.equals(requestItem.getItem().getIsReusable());

                IssuedItem issuedItem = new IssuedItem();

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

                if (reusable) {

                        AssetItem asset =
                                assetItemRepository
                                        .findById(assetItemId)
                                        .orElseThrow(() ->
                                                new RuntimeException(
                                                        "Asset not found"
                                                )
                                        );

                        if(asset.getAssetStatus() != AssetStatus.AVAILABLE) {

                                throw new RuntimeException(
                                        "Selected asset is not available"
                                );
                        }

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
                                                                requestItem.getItem()
                                                                        .getItemId()
                                                        )
                                        )
                                        .findFirst()
                                        .orElseThrow(() ->
                                                new RuntimeException(
                                                        "Stock not found"
                                                )
                                        );

                        Integer alreadyIssued =
                                issuedItemRepository
                                        .getIssuedQuantityForRequestItem(
                                                requestItem.getRequestItemId()
                                        );

                        int remaining =
                                requestItem.getRequestedQuantity()
                                        - alreadyIssued;

                        if (remaining <= 0) {

                                throw new RuntimeException(
                                        "Item already fully issued"
                                );
                        }

                        if (issueQuantity > remaining) {

                                throw new RuntimeException(
                                        "Cannot issue more than remaining quantity"
                                );
                        }

                        if (stock.getAvailableQuantity() < issueQuantity) {

                                throw new RuntimeException(
                                        "Insufficient stock"
                                );
                        }

                        stock.setAvailableQuantity(
                                stock.getAvailableQuantity()
                                - issueQuantity
                        );

                        stock.setIssuedQuantity(
                                stock.getIssuedQuantity()
                                + issueQuantity
                        );

                        issuedItem.setIssuedQuantity(
                                issueQuantity
                        );

                        inventoryStockRepository.save(stock);
                }

                issuedItem.setIssueStatus(
                        IssueStatus.ISSUED
                );

                issuedItemRepository.save(
                        issuedItem
                );

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

                boolean fullyIssued =
                        request.getRequestItems()
                                .stream()
                                .allMatch(item -> {

                                        Integer issuedQty = issuedItemRepository
                                                        .getIssuedQuantityForRequestItem(item.getRequestItemId());

                                        return issuedQty
                                                >= item.getRequestedQuantity();
                                });

                if (fullyIssued) {

                        request.setRequestStatus(
                                RequestStatus.ISSUED
                        );

                } else {

                        request.setRequestStatus(
                                RequestStatus.PARTIALLY_ISSUED
                        );
                }

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

                                .filter(item -> item.getIssueStatus() == IssueStatus.ISSUED)

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