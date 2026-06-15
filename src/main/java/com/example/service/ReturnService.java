package com.example.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dto.ReturnedItemDTO;
import com.example.entity.AssetItem;
import com.example.entity.Employee;
import com.example.entity.IssuedItem;
import com.example.entity.ReturnedItem;
import com.example.enums.AssetStatus;
import com.example.enums.IssueStatus;
import com.example.enums.ReturnCondition;
import com.example.repository.AssetItemRepository;
import com.example.repository.EmployeeRepository;
import com.example.repository.IssuedItemRepository;
import com.example.repository.ReturnedItemRepository;
import com.example.security.SecurityService;

@Service
public class ReturnService {

        private final IssuedItemRepository issuedItemRepository;

        private final ReturnedItemRepository returnedItemRepository;

        private final SecurityService securityService;

        private final EmployeeRepository employeeRepository;

        private final AssetItemRepository assetItemRepository;

        private final AuditLogService auditLogService;

        public ReturnService(
                IssuedItemRepository issuedItemRepository,
                ReturnedItemRepository returnedItemRepository,
                SecurityService securityService,
                EmployeeRepository employeeRepository,
                AuditLogService auditLogService,
                AssetItemRepository assetItemRepository
        ){

                this.issuedItemRepository = issuedItemRepository;

                this.returnedItemRepository = returnedItemRepository;

                this.securityService = securityService;

                this.employeeRepository = employeeRepository;

                this.auditLogService = auditLogService;

                this.assetItemRepository = assetItemRepository;
        }

        public List<ReturnedItemDTO> getIssuedItemsForReturn() {

                String username = securityService.getAuthenticatedUser();
                String role = securityService.getAuthenticatedRole();

                Employee loggedInUser = employeeRepository
                                .findAll()
                                .stream()
                                .filter(user -> user.getUsername().equals(username))
                                .findFirst()
                                .orElseThrow(() ->
                                        new RuntimeException("User not found")
                                );

                return issuedItemRepository
                        .findAll()
                        .stream()
                        .filter(item ->

                                Boolean.TRUE.equals(item.getRequestItem().getItem().getIsReusable()) 
                                                && item.getIssueStatus() == IssueStatus.ISSUED 
                                                || item.getIssueStatus() == IssueStatus.RETURN_REJECTED
                        )
                        .filter(item -> {
                                if(role.equals("ROLE_SUPER_ADMIN") || role.equals("ROLE_INVENTORY_ADMIN")) {
                                        return true;
                                }

                                return item.getIssuedToEmployee() != null
                                        && loggedInUser != null
                                        && item.getIssuedToEmployee().getEmployeeId().equals(loggedInUser.getEmployeeId());
                        })
                        .sorted(
                                Comparator.comparing(item -> item.getIssuedDate())
                        )
                        .map(item -> {

                                ReturnedItemDTO dto = new ReturnedItemDTO();

                                dto.setIssuedItemId(item.getIssuedItemId());

                                dto.setIssueReferenceNumber(item.getIssueReferenceNumber());

                                dto.setEmployeeName(item.getIssuedToEmployee().getEmployeeName());

                                dto.setItemName(item.getRequestItem().getItem().getItemName());

                                dto.setItemCode(item.getRequestItem().getItem().getItemCode());

                                dto.setIssuedQuantity(item.getIssuedQuantity());

                                if(item.getAssetItem() != null) {
                                        dto.setAssetReferenceNumber(item.getAssetItem().getAssetReferenceNumber());
                                }

                                dto.setReturnQuantity(item.getIssuedQuantity());

                                dto.setIssueStatus(item.getIssueStatus());

                                return dto;
                        })

                        .collect(Collectors.toList());
        }

        public void returnItem(ReturnedItemDTO dto) {

                IssuedItem issuedItem = issuedItemRepository
                                .findById(dto.getIssuedItemId())
                                .orElseThrow(() ->
                                        new RuntimeException("Issued item not found")
                                );

                if (issuedItem.getIssueStatus() == IssueStatus.RETURN_PENDING) {
                        throw new RuntimeException(
                                "Return request already submitted for this item"
                        );
                }

                if (issuedItem.getIssueStatus() == IssueStatus.CLOSED) {
                        throw new RuntimeException(
                                "This item is already returned and closed"
                        );
                }

                ReturnedItem returnedItem = new ReturnedItem();

                returnedItem.setIssuedItem(issuedItem);

                if (issuedItem.getAssetItem() != null) {
                        returnedItem.setAssetItem(issuedItem.getAssetItem());
                }

                returnedItem.setReturnReferenceNumber(generateReturnReference());

                returnedItem.setReturnedQuantity(dto.getReturnQuantity());

                returnedItem.setReturnCondition(dto.getReturnCondition());

                returnedItem.setReturnRemarks(dto.getReturnRemarks());

                returnedItem.setReturnedDate(LocalDateTime.now());

                returnedItemRepository.save(returnedItem);

                issuedItem.setIssueStatus(IssueStatus.RETURN_PENDING);

                issuedItemRepository.save(issuedItem);

                auditLogService.logAction(
                        "RETURN_MODULE",
                        "RETURN_REQUEST",
                        "Return request submitted : " + issuedItem.getIssueReferenceNumber()
                );
        }

        public List<ReturnedItemDTO> getReturnedHistory(Employee employee) {

                boolean isAdmin = 
                                employee.getRole() != null
                                &&
                                (
                                        "SUPER_ADMIN".equals(employee.getRole().getRoleName())
                                        ||
                                        "INVENTORY_ADMIN".equals(employee.getRole().getRoleName())
                                );

                return returnedItemRepository
                        .findAll()
                        .stream()
                        .filter(item ->
                                isAdmin
                                ||
                                item.getIssuedItem().getIssuedToEmployee().getEmployeeId().equals(employee.getEmployeeId())
                        )
                        .sorted(Comparator.comparing(ReturnedItem::getReturnedDate).reversed())
                        .map(item -> {
                                ReturnedItemDTO dto = new ReturnedItemDTO();

                                dto.setIssuedItemId(item.getIssuedItem().getIssuedItemId());

                                dto.setReturnedItemId(item.getReturnedItemId());

                                dto.setIssueReferenceNumber(item.getIssuedItem().getIssueReferenceNumber());

                                dto.setReturnReferenceNumber(item.getReturnReferenceNumber());

                                dto.setItemName(item.getIssuedItem().getRequestItem().getItem().getItemName());

                                dto.setItemCode(item.getIssuedItem().getRequestItem().getItem().getItemCode());

                                dto.setReturnQuantity(item.getReturnedQuantity());

                                if(item.getIssuedItem().getAssetItem() != null) {
                                        dto.setAssetReferenceNumber(item.getIssuedItem().getAssetItem().getAssetReferenceNumber());
                                }

                                dto.setIssueStatus(item.getIssuedItem().getIssueStatus());

                                return dto;
                        })
                        .toList();
        }

        public void cancelReturn(Long returnedItemId) {

                ReturnedItem returnedItem = returnedItemRepository
                                .findById(returnedItemId)
                                .orElseThrow(() -> new RuntimeException("Returned item not found"));

                IssuedItem issuedItem = returnedItem.getIssuedItem();

                boolean reusable = Boolean.TRUE.equals(issuedItem.getRequestItem().getItem().getIsReusable());

                if(reusable) {

                        AssetItem asset = issuedItem.getAssetItem();

                        if(asset != null) {
                                asset.setAssetStatus(AssetStatus.AVAILABLE);
                                assetItemRepository.save(asset);
                        }

                }

                issuedItem.setIssueStatus(IssueStatus.ISSUED);

                issuedItemRepository.save(issuedItem);

                returnedItemRepository.delete(returnedItem);

                auditLogService.logAction(

                        "RETURN_MODULE",

                        "CANCEL_RETURN",

                        "Cancelled returned item : " + issuedItem.getIssueReferenceNumber()
                );
        }

        public void closeReturn(Long returnedItemId) {

                ReturnedItem returnedItem = returnedItemRepository
                                .findById(returnedItemId)
                                .orElseThrow(() ->
                                        new RuntimeException("Returned item not found")
                                );

                IssuedItem issuedItem = returnedItem.getIssuedItem();

                boolean reusable = Boolean.TRUE.equals(issuedItem.getRequestItem().getItem().getIsReusable());

                if(reusable) {

                        AssetItem asset = issuedItem.getAssetItem();

                        if(asset != null) {
                                if(returnedItem.getReturnCondition() == ReturnCondition.DAMAGED) {
                                        asset.setAssetStatus(AssetStatus.DAMAGED);
                                } 
                                
                                else {
                                        asset.setAssetStatus(AssetStatus.AVAILABLE);
                                }

                                assetItemRepository.save(asset);
                        }

                } 
                // else {

                //         InventoryStock stock = inventoryStockRepository
                //                         .findAll()
                //                         .stream()
                //                         .filter(s ->

                //                                 s.getItem()
                //                                         .getItemId()
                //                                         .equals(
                //                                                 issuedItem.getRequestItem().getItem().getItemId()
                //                                         )
                //                         )
                //                         .findFirst()
                //                         .orElseThrow(() ->
                //                                 new RuntimeException("Stock not found")
                //                         );

                //         if(returnedItem.getReturnCondition() == ReturnCondition.DAMAGED) {

                //                 stock.setDamagedQuantity(
                //                         stock.getDamagedQuantity() + returnedItem.getReturnedQuantity()
                //                 );

                //         } 
                //         else {

                //                 stock.setAvailableQuantity(
                //                         stock.getAvailableQuantity() + returnedItem.getReturnedQuantity()
                //                 );
                //         }

                //         stock.setIssuedQuantity(
                //                 stock.getIssuedQuantity() - returnedItem.getReturnedQuantity()
                //         );

                //         inventoryStockRepository.save(stock);
                // }

                issuedItem.setIssueStatus(IssueStatus.CLOSED);

                issuedItemRepository.save(issuedItem);

                auditLogService.logAction(

                        "RETURN_MODULE",

                        "CLOSE_RETURN",

                        "Closed return : " + issuedItem.getIssueReferenceNumber()
                );
        }


        private String generateReturnReference() {

                return "RETURN-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
        }
}