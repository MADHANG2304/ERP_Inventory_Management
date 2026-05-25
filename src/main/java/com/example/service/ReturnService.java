package com.example.service;

import com.example.dto.ReturnedItemDTO;
import com.example.entity.*;
import com.example.enums.IssueStatus;
import com.example.enums.ReferenceType;
import com.example.enums.ReturnCondition;
import com.example.enums.TransactionType;
import com.example.repository.*;
import com.example.security.SecurityService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReturnService {

    private final IssuedItemRepository issuedItemRepository;

    private final ReturnedItemRepository returnedItemRepository;

    private final InventoryStockRepository inventoryStockRepository;

    private final InventoryTransactionRepository inventoryTransactionRepository;

    private final SecurityService securityService;

    private final UserRepository userRepository;

    public ReturnService(
            IssuedItemRepository issuedItemRepository,
            ReturnedItemRepository returnedItemRepository,
            InventoryStockRepository inventoryStockRepository,
            InventoryTransactionRepository inventoryTransactionRepository,
            SecurityService securityService,
            UserRepository userRepository
        ) {

        this.issuedItemRepository = issuedItemRepository;

        this.returnedItemRepository = returnedItemRepository;

        this.inventoryStockRepository = inventoryStockRepository;

        this.inventoryTransactionRepository = inventoryTransactionRepository;

        this.securityService = securityService;

        this.userRepository = userRepository;
    }

    public List<ReturnedItemDTO> getIssuedItemsForReturn() {

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

                        Boolean.TRUE.equals(

                                item.getRequestItem()
                                        .getItem()
                                        .getAllowReturn()
                        )

                        &&

                        item.getIssueStatus()
                                != IssueStatus.RETURNED
                )

                // IMPORTANT FIX
                .filter(item -> {

                        // ADMIN CAN SEE EVERYTHING
                        if(role.equals("ROLE_SUPER_ADMIN")
                                || role.equals("ROLE_INVENTORY_ADMIN")) {

                        return true;
                        }

                        // EMPLOYEE CAN SEE ONLY THEIR ITEMS
                        return item.getIssuedToEmployee() != null
                                && loggedInUser.getEmployee() != null
                                && item.getIssuedToEmployee()
                                        .getEmployeeId()
                                        .equals(
                                                loggedInUser
                                                        .getEmployee()
                                                        .getEmployeeId()
                                        );
                })

                .map(item -> {

                        ReturnedItemDTO dto =
                                new ReturnedItemDTO();

                        dto.setIssuedItemId(
                                item.getIssuedItemId()
                        );

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

                        dto.setReturnQuantity(
                                item.getIssuedQuantity()
                        );

                        dto.setIssueStatus(
                                item.getIssueStatus()
                        );

                        return dto;
                })

                .collect(Collectors.toList());
        }

    public void returnItem(
            ReturnedItemDTO dto) {

        IssuedItem issuedItem = issuedItemRepository
                .findById(dto.getIssuedItemId())
                .orElseThrow(() -> new RuntimeException(
                        "Issued item not found"));

        if (dto.getReturnQuantity() > issuedItem.getIssuedQuantity()) {

            throw new RuntimeException(
                    "Return quantity exceeds issued quantity");
        }

        ReturnedItem returnedItem = new ReturnedItem();

        returnedItem.setIssuedItem(
                issuedItem);

        returnedItem.setReturnedQuantity(
                dto.getReturnQuantity());

        returnedItem.setReturnCondition(
                dto.getReturnCondition());

        returnedItem.setReturnRemarks(
                dto.getReturnRemarks());

        returnedItem.setReturnedDate(
                LocalDateTime.now());

        returnedItemRepository.save(
                returnedItem);

        int remainingQuantity =

                issuedItem.getIssuedQuantity()

                        - dto.getReturnQuantity();

        if (issuedItem.getIssueStatus() == null) {

            issuedItem.setIssueStatus(
                    IssueStatus.ISSUED);
        }

        if (remainingQuantity == 0) {

            issuedItem.setIssueStatus(
                    IssueStatus.RETURNED);

        } else {

            issuedItem.setIssueStatus(
                    IssueStatus.PARTIALLY_RETURNED);
        }

        issuedItem.setIssuedQuantity(
                remainingQuantity);

        issuedItemRepository.save(
                issuedItem);

        InventoryStock stock = inventoryStockRepository
                .findAll()
                .stream()
                .filter(s ->

                s.getItem()
                        .getItemId()
                        .equals(

                                issuedItem
                                        .getRequestItem()
                                        .getItem()
                                        .getItemId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Stock not found"));

        if (dto.getReturnCondition() == ReturnCondition.GOOD) {

            stock.setAvailableQuantity(

                    stock.getAvailableQuantity()

                            + dto.getReturnQuantity());
        }

        else if (dto.getReturnCondition() == ReturnCondition.DAMAGED) {

            stock.setDamagedQuantity(

                    stock.getDamagedQuantity()

                            + dto.getReturnQuantity());
        }

        stock.setIssuedQuantity(

                stock.getIssuedQuantity()

                        - dto.getReturnQuantity());

        inventoryStockRepository.save(
                stock);

        InventoryTransaction transaction = new InventoryTransaction();

        transaction.setItem(
                issuedItem.getRequestItem()
                        .getItem());

        transaction.setTransactionType(
                TransactionType.RETURN);

        transaction.setReferenceType(ReferenceType.RETURN_REQUEST);

        transaction.setReferenceNumber(
        issuedItem.getIssueReferenceNumber());

        transaction.setQuantity(
                dto.getReturnQuantity());

        transaction.setTransactionDate(
                LocalDateTime.now());

        transaction.setRemarks(
                "Returned item - "
                        + dto.getReturnCondition()
                                .name());

        inventoryTransactionRepository
                .save(transaction);
    }
}