package com.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dto.IssuedItemDTO;
import com.example.entity.InventoryRequest;
import com.example.entity.InventoryStock;
import com.example.entity.InventoryTransaction;
import com.example.entity.IssuedItem;
import com.example.entity.RequestItems;
import com.example.entity.User;
import com.example.enums.IssueStatus;
import com.example.enums.RequestStatus;
import com.example.enums.TransactionType;
import com.example.repository.InventoryRequestRepository;
import com.example.repository.InventoryStockRepository;
import com.example.repository.InventoryTransactionRepository;
import com.example.repository.IssuedItemRepository;
import com.example.repository.RequestItemRepository;
import com.example.repository.UserRepository;
import com.example.security.SecurityService;

@Service
public class IssueService {

    private final InventoryRequestRepository
            inventoryRequestRepository;

    private final InventoryStockRepository
            inventoryStockRepository;

    private final InventoryTransactionRepository
            inventoryTransactionRepository;

    private final IssuedItemRepository
            issuedItemRepository;

    private final RequestItemRepository
            requestItemsRepository;

    private final UserRepository
            userRepository;
    
    private final SecurityService securityService;


    private final AuditLogService auditLogService;

    public IssueService(
            InventoryRequestRepository inventoryRequestRepository,
            InventoryStockRepository inventoryStockRepository,
            InventoryTransactionRepository inventoryTransactionRepository,
            IssuedItemRepository issuedItemRepository,
            RequestItemRepository requestItemsRepository,
            UserRepository userRepository,
            AuditLogService auditLogService,
            SecurityService securityService
    ) {

        this.inventoryRequestRepository =
                inventoryRequestRepository;

        this.inventoryStockRepository =
                inventoryStockRepository;

        this.inventoryTransactionRepository =
                inventoryTransactionRepository;

        this.issuedItemRepository =
                issuedItemRepository;

        this.requestItemsRepository =
                requestItemsRepository;

        this.userRepository =
                userRepository;

        this.auditLogService =
                auditLogService;

        this.securityService = 
                securityService;
    }

    public List<IssuedItemDTO> getApprovedRequests() {

        return inventoryRequestRepository
                .findAll()
                .stream()

                .filter(request ->
                        request.getRequestStatus() == RequestStatus.APPROVED ||  request.getRequestStatus() == RequestStatus.ISSUED
                )

                .flatMap(request ->

                        request.getRequestItems()
                                .stream()

                                .filter(item -> {

                                    boolean alreadyIssued =
                                            issuedItemRepository
                                                    .findAll()
                                                    .stream()
                                                    .anyMatch(issued ->
                                                            issued
                                                                    .getRequestItem()
                                                                    .getRequestItemId()
                                                                    .equals(item.getRequestItemId())
                                                     );

                                    return !alreadyIssued;
                                })

                                .map(item -> {

                                    IssuedItemDTO dto = new IssuedItemDTO();

                                    dto.setRequestId(request.getRequestId());

                                    dto.setRequestItemId(item.getRequestItemId());

                                    dto.setRequestNumber(request.getRequestNumber());

                                    dto.setEmployeeName(request.getEmployee().getEmployeeName());

                                    dto.setItemName(item.getItem().getItemName());

                                    dto.setItemCode(item.getItem().getItemCode());

                                    dto.setRequestedQuantity(item.getRequestedQuantity());

                                    dto.setIssuedQuantity(item.getRequestedQuantity());

                                    return dto;
                                })
                )

                .collect(Collectors.toList());
    }

    public void issueItem(Long requestItemId, String username) {

        RequestItems requestItem =
                requestItemsRepository
                        .findById(requestItemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Request item not found"
                                )
                        );

        InventoryRequest request = requestItem.getRequest();

        User issuedBy =
                userRepository.findAll()
                        .stream()
                        .filter(user -> user.getUsername().equals(username))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        InventoryStock stock =
                inventoryStockRepository
                        .findAll()
                        .stream()
                        .filter(s ->
                                s.getItem().getItemId().equals(requestItem.getItem().getItemId())
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException("Stock not found")
                        );

        if(stock.getAvailableQuantity() < requestItem.getRequestedQuantity()) {

            throw new RuntimeException("Insufficient stock for " + requestItem.getItem().getItemName());
        }

        stock.setAvailableQuantity(stock.getAvailableQuantity() - requestItem.getRequestedQuantity());

        stock.setIssuedQuantity(stock.getIssuedQuantity() + requestItem.getRequestedQuantity());

        inventoryStockRepository.save(stock);


        IssuedItem issuedItem = new IssuedItem();

        issuedItem.setRequest(request);

        issuedItem.setRequestItem(requestItem);

        issuedItem.setIssuedToEmployee(request.getEmployee());

        issuedItem.setIssuedBy(issuedBy);

        issuedItem.setIssuedQuantity(requestItem.getRequestedQuantity());

        issuedItem.setIssuedDate(LocalDateTime.now());

        issuedItem.setIssueReferenceNumber(generateReferenceNumber());


        InventoryTransaction transaction = new InventoryTransaction();

        transaction.setItem(requestItem.getItem());

        transaction.setTransactionType(TransactionType.ISSUE);

        transaction.setQuantity(requestItem.getRequestedQuantity());

        transaction.setTransactionDate(LocalDateTime.now());

        transaction.setReferenceNumber(issuedItem.getIssueReferenceNumber());

        transaction.setRemarks("Item issued for request " + request.getRequestNumber());

        inventoryTransactionRepository.save(transaction);


        issuedItem.setIssueStatus(IssueStatus.ISSUED);

        issuedItemRepository.save(issuedItem);
        

        long totalItems = request.getRequestItems().size();

        long issuedItems =
                request.getRequestItems()
                        .stream()
                        .filter(item ->
                                issuedItemRepository
                                        .findAll()
                                        .stream()
                                        .anyMatch(issued ->
                                                issued.getRequestItem().getRequestItemId()
                                                        .equals(item.getRequestItemId())
                                        )
                        )
                        .count();

        if(issuedItems == totalItems) {

            request.setRequestStatus(RequestStatus.ISSUED);

        } else {

            request.setRequestStatus(RequestStatus.APPROVED);
        }

        inventoryRequestRepository.save(request);

        auditLogService.logAction(
                "ISSUE_MODULE",
                "ISSUE",
                "Issued item : " + requestItem.getItem().getItemName()
        );
    }

    public List<IssuedItemDTO> getIssuedHistory() {

        String username =
                securityService.getAuthenticatedUser();

        String role =
                securityService.getAuthenticatedRole();

        User loggedInUser =
                userRepository
                        .findAll()
                        .stream()

                        .filter(user ->
                                user.getUsername()
                                        .equals(username)
                        )

                        .findFirst()

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        return issuedItemRepository
                .findAll()
                .stream()

                .filter(item ->

                        // Boolean.TRUE.equals(item.getRequestItem().getItem().getAllowReturn()) 
                        //                 && 
                                        item.getIssueStatus() != IssueStatus.RETURNED
                )

                .filter(item -> {

                        if(role.equals("ROLE_SUPER_ADMIN")
                                || role.equals("ROLE_INVENTORY_ADMIN")) {

                                return true;
                        }

                        return item.getIssuedToEmployee() != null
                                && loggedInUser.getEmployee() != null
                                && item.getIssuedToEmployee()
                                        .getEmployeeId()
                                        .equals(loggedInUser.getEmployee().getEmployeeId());
                }) 

                .map(item -> {

                        IssuedItemDTO dto =
                                new IssuedItemDTO();

                        dto.setIssueReferenceNumber(
                                item.getIssueReferenceNumber()
                        );

                        dto.setEmployeeName(
                                item.getIssuedToEmployee()
                                        .getEmployeeName()
                        );

                        dto.setItemName(
                                item.getRequestItem()
                                        .getItem()
                                        .getItemName()
                        );

                        dto.setItemCode(
                                item.getRequestItem()
                                        .getItem()
                                        .getItemCode()
                        );

                        dto.setIssuedQuantity(
                                item.getIssuedQuantity()
                        );

                        return dto;
                })

                .toList();
}

    private String generateReferenceNumber() {

        return "ISSUE-"
                + UUID.randomUUID()
                        .toString()
                        .substring(0, 8);
    }
}