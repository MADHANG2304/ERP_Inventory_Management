package com.example.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dto.CurrentAssetDTO;
import com.example.dto.CurrentConsumableDTO;
import com.example.dto.ReturnedItemDTO;
import com.example.entity.Employee;
import com.example.entity.IssuedItem;
import com.example.entity.ReturnedItem;
import com.example.enums.IssueStatus;
import com.example.repository.EmployeeRepository;
import com.example.repository.IssuedItemRepository;
import com.example.repository.ReturnedItemRepository;
import com.example.security.SecurityService;

@Service
public class MyCurrentStockService {

        private final IssuedItemRepository issuedItemRepository;

        private final ReturnedItemRepository returnedItemRepository;

        private final EmployeeRepository employeeRepository;

        private final SecurityService securityService;

        public MyCurrentStockService(
                IssuedItemRepository issuedItemRepository,
                ReturnedItemRepository returnedItemRepository,
                EmployeeRepository employeeRepository,
                SecurityService securityService
        ){

                this.issuedItemRepository = issuedItemRepository;

                this.returnedItemRepository = returnedItemRepository;

                this.employeeRepository = employeeRepository;

                this.securityService = securityService;
        }

        private Employee getLoggedInEmployee() {

                return employeeRepository.findByUsername(
                        securityService.getAuthenticatedUser()
                );
        }

        public List<CurrentAssetDTO> getCurrentAssets() {

                Employee employee = getLoggedInEmployee();

                return issuedItemRepository
                        .findAll()
                        .stream()
                        .filter(item ->

                                item.getIssuedToEmployee() != null

                                &&

                                item.getIssuedToEmployee().getEmployeeId().equals(employee.getEmployeeId())

                                &&

                                item.getAssetItem() != null

                                &&

                                item.getIssueStatus() == IssueStatus.ISSUED
                        )

                        .map(item -> {

                                CurrentAssetDTO dto = new CurrentAssetDTO();

                                dto.setIssuedItemId(item.getIssuedItemId());

                                dto.setIssueReferenceNumber(item.getIssueReferenceNumber());

                                dto.setItemName(item.getRequestItem().getItem().getItemName());

                                dto.setItemCode(item.getRequestItem().getItem().getItemCode());

                                dto.setAssetReferenceNumber(item.getAssetItem().getAssetReferenceNumber());

                                dto.setModelNumber(item.getAssetItem().getModelNumber());

                                dto.setPurchaseDate(item.getAssetItem().getPurchaseDate());

                                dto.setIssuedDate(item.getIssuedDate());

                                return dto;
                        })

                        .toList();
        }

        public List<CurrentConsumableDTO> getCurrentConsumables() {

                Employee employee = getLoggedInEmployee();

                Map<Long, List<IssuedItem>> groupedItems =

                        issuedItemRepository
                                .findAll()
                                .stream()
                                .filter(item ->

                                        item.getIssuedToEmployee() != null

                                        &&

                                        item.getIssuedToEmployee().getEmployeeId().equals(employee.getEmployeeId())

                                        &&

                                        item.getAssetItem() == null
                                )
                                .collect(
                                        Collectors.groupingBy(item -> item.getRequestItem().getItem().getItemId())
                                );

                return groupedItems.values()
                        .stream()
                        .map(items -> {

                                IssuedItem first = items.get(0);

                                int totalIssued = items.stream()
                                                .mapToInt(IssuedItem::getIssuedQuantity)
                                                .sum();

                                // int totalReturned = returnedItemRepository
                                //                 .findAll()
                                //                 .stream()
                                //                 .filter(returnItem ->
                                //                         returnItem.getIssuedItem()
                                //                                 .getIssuedToEmployee()
                                //                                 .getEmployeeId()
                                //                                 .equals(employee.getEmployeeId())

                                //                         &&

                                //                         returnItem.getIssuedItem()
                                //                                 .getRequestItem()
                                //                                 .getItem()
                                //                                 .getItemId()
                                //                                 .equals(first.getRequestItem().getItem().getItemId())
                                //                 )
                                //                 .mapToInt(ReturnedItem::getReturnedQuantity)
                                //                 .sum();

                                CurrentConsumableDTO dto = new CurrentConsumableDTO();

                                dto.setItemId(first.getRequestItem().getItem().getItemId());

                                dto.setItemName(first.getRequestItem().getItem().getItemName());

                                dto.setItemCode(first.getRequestItem().getItem().getItemCode());

                                dto.setTotalIssued(totalIssued);

                                // dto.setTotalReturned(totalReturned);
                                // dto.setCurrentBalance(totalIssued - totalReturned);

                                dto.setCurrentBalance(totalIssued);

                                return dto;
                        })
                        .filter(dto -> dto.getCurrentBalance() > 0)
                        .toList();
        }

        public List<ReturnedItemDTO> getPendingReturns() {

                Employee employee = getLoggedInEmployee();

                return returnedItemRepository
                        .findAll()
                        .stream()
                        .filter(item ->
                                item.getIssuedItem()
                                        .getIssuedToEmployee()
                                        .getEmployeeId()
                                        .equals(employee.getEmployeeId())

                                &&

                                item.getIssuedItem().getIssueStatus() == IssueStatus.RETURN_PENDING
                        )
                        .map(item -> {

                                ReturnedItemDTO dto = new ReturnedItemDTO();

                                dto.setReturnReferenceNumber(item.getReturnReferenceNumber());

                                dto.setIssueReferenceNumber(item.getIssuedItem().getIssueReferenceNumber());

                                dto.setItemName(
                                        item.getIssuedItem()
                                                .getRequestItem()
                                                .getItem()
                                                .getItemName()
                                );

                                dto.setItemCode(
                                        item.getIssuedItem()
                                                .getRequestItem()
                                                .getItem()
                                                .getItemCode()
                                );

                                dto.setReturnedDate(item.getReturnedDate());

                                dto.setIssueStatus(item.getIssuedItem().getIssueStatus());

                                return dto;
                        })
                        .toList();
        }
}