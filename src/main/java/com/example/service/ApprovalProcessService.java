package com.example.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.dto.ApprovalFilterDTO;
import com.example.dto.RequestApprovalDTO;
import com.example.dto.RequestItemDTO;
import com.example.entity.Employee;
import com.example.entity.InventoryRequest;
import com.example.entity.RequestApproval;
import com.example.entity.RequestItems;
import com.example.enums.ApprovalStatus;
import com.example.enums.RequestStatus;
import com.example.repository.EmployeeRepository;
import com.example.repository.InventoryRequestRepository;
import com.example.repository.RequestApprovalRepository;
import com.example.repository.RequestItemRepository;
import com.example.specification.ApprovalSpecification;

@Service
public class ApprovalProcessService {

        private final RequestApprovalRepository requestApprovalRepository;

        private final InventoryRequestRepository inventoryRequestRepository;

        private final EmployeeRepository employeeRepository;

        private final AuditLogService auditLogService;

        private final RequestItemRepository requestItemRepository;

        public ApprovalProcessService(
                        RequestApprovalRepository requestApprovalRepository,
                        InventoryRequestRepository inventoryRequestRepository,
                        EmployeeRepository employeeRepository,
                        AuditLogService auditLogService,
                        RequestItemRepository requestItemRepository) {

                this.requestApprovalRepository = requestApprovalRepository;

                this.inventoryRequestRepository = inventoryRequestRepository;

                this.employeeRepository = employeeRepository;

                this.auditLogService = auditLogService;

                this.requestItemRepository = requestItemRepository;
        }

        public List<RequestApprovalDTO> getPendingApprovals(String username) {

                Employee employee = employeeRepository.findAll()
                                .stream()
                                .filter(u -> u.getUsername().equals(username))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return requestApprovalRepository
                                .findAll()
                                .stream()
                                .filter(approval ->
                                                Boolean.TRUE.equals(approval.getIsCurrentLevel())

                                                &&

                                                approval.getApprovalStatus() == ApprovalStatus.PENDING

                                                &&

                                                approval.getApprover() != null

                                                &&

                                                approval.getApprover().getEmployeeId() != null

                                                &&

                                                approval.getApprover().getEmployeeId().equals(employee.getEmployeeId()))

                                .sorted(
                                        Comparator.comparing(approval -> approval.getRequest().getRequestDate())
                                )
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
        }

        public void approveRequest(Long approvalId, String comments, List<RequestItemDTO> approvedItems)
        {

                RequestApproval currentApproval = requestApprovalRepository
                                .findById(approvalId)
                                .orElseThrow(() -> new RuntimeException("Approval not found"));

                if (!Boolean.TRUE.equals(currentApproval.getIsCurrentLevel())) {
                        throw new RuntimeException("Invalid approval level");
                }

                InventoryRequest request = currentApproval.getRequest();

                boolean atleastOneApproved = false;

                for (RequestItemDTO dto : approvedItems) {

                        RequestItems requestItem =
                                request.getRequestItems()
                                        .stream()
                                        .filter(item ->
                                                item.getRequestItemId().equals(dto.getRequestItemId())
                                        )
                                        .findFirst()
                                        .orElseThrow(() ->
                                                new RuntimeException("Request item not found")
                                        );

                        if(Boolean.TRUE.equals(dto.getSelected())) {

                                Integer approvedQty = dto.getApprovedQuantity() == null ? 0 : dto.getApprovedQuantity();

                                if(approvedQty > requestItem.getRequestedQuantity()) {
                                        throw new RuntimeException(
                                                requestItem.getItem().getItemName() + " approved quantity cannot exceed requested quantity"
                                        );
                                }

                                requestItem.setApprovedQuantity(approvedQty);

                                requestItemRepository.save(requestItem);

                                if(approvedQty > 0) {
                                        atleastOneApproved = true;
                                }

                        } 
                        else {
                                requestItem.setApprovedQuantity(0);

                                requestItemRepository.save(requestItem);
                        }
                }

                inventoryRequestRepository.save(request);

                if(!atleastOneApproved) {

                        currentApproval.setApprovalStatus(ApprovalStatus.REJECTED);

                        currentApproval.setComments(comments);

                        currentApproval.setIsCurrentLevel(false);

                        requestApprovalRepository.save(currentApproval);

                        request.setRequestStatus(RequestStatus.REJECTED);

                        inventoryRequestRepository.save(request);

                        auditLogService.logAction(

                                "APPROVAL_MODULE",

                                "REJECT",

                                "Rejected request : " + request.getRequestNumber()
                        );

                        return;
                }

                currentApproval.setApprovalStatus(ApprovalStatus.APPROVED);

                currentApproval.setComments(comments);

                currentApproval.setIsCurrentLevel(false);

                requestApprovalRepository.save(currentApproval);

                List<RequestApproval> approvals =
                        request.getApprovals()
                                .stream()
                                .sorted(
                                        Comparator.comparing(RequestApproval::getApprovalOrder)
                                )
                                .toList();

                RequestApproval nextApproval =
                        approvals.stream()
                                .filter(approval ->
                                        approval.getApprovalOrder() == currentApproval.getApprovalOrder() + 1
                                        &&
                                        approval.getApprovalStatus() == ApprovalStatus.PENDING
                                )
                                .findFirst()
                                .orElse(null);

                if(nextApproval != null) {

                        nextApproval.setIsCurrentLevel(true);

                        requestApprovalRepository.save(nextApproval);

                } else {

                        request.setRequestStatus(RequestStatus.APPROVED);

                        inventoryRequestRepository.save(request);

                        auditLogService.logAction(

                                "APPROVAL_MODULE",

                                "APPROVE",

                                "Approved request : " + request.getRequestNumber()
                        );
                }
        }

        public void rejectRequest(Long approvalId, String comments) {

                RequestApproval currentApproval = requestApprovalRepository
                                .findById(approvalId)
                                .orElseThrow(() -> new RuntimeException("Approval not found"));

                currentApproval.setApprovalStatus(ApprovalStatus.REJECTED);

                currentApproval.setComments(comments);

                currentApproval.setIsCurrentLevel(false);

                requestApprovalRepository.save(currentApproval);

                InventoryRequest request = currentApproval.getRequest();

                request.setRequestStatus(RequestStatus.REJECTED);

                inventoryRequestRepository.save(request);

                auditLogService.logAction(

                        "APPROVAL_MODULE",

                        "REJECT",

                        "Rejected request : " + request.getRequestNumber()
                );
        }

        public List<RequestApprovalDTO> filterApprovals(ApprovalFilterDTO filterDTO) {

                Specification<RequestApproval> specification = ApprovalSpecification
                                                .hasRequestNumber(filterDTO.getRequestNumber())

                                                .and(ApprovalSpecification.hasApprovalStatus(filterDTO.getApprovalStatus()))

                                                .and(ApprovalSpecification.hasApprovalRole(filterDTO.getApprovalRole()))

                                                .and(ApprovalSpecification.hasCurrentLevel(filterDTO.getCurrentLevel()));

                return requestApprovalRepository
                                .findAll(specification)
                                .stream()
                                .map(this::convertToDTO)
                                .toList();
        }

        private RequestApprovalDTO convertToDTO(RequestApproval approval) {

                RequestApprovalDTO dto = new RequestApprovalDTO();

                dto.setApprovalId(approval.getApprovalId());

                dto.setRequestId(approval.getRequest().getRequestId());

                dto.setEmployeeId(
                        approval.getRequest()
                                .getEmployee()
                                .getEmployeeId()
                );

                dto.setEmployeeName(
                        approval.getRequest()
                                .getEmployee()
                                .getEmployeeName()
                );

                dto.setRequestNumber(approval.getRequest().getRequestNumber());

                dto.setApprovalOrder(approval.getApprovalOrder());

                dto.setApprovalRole(approval.getApprovalRole());

                dto.setApprovalStatus(approval.getApprovalStatus());

                dto.setIsCurrentLevel(approval.getIsCurrentLevel());

                dto.setComments(approval.getComments());

                dto.setRemarks(approval.getRequest().getRemarks());

                dto.setRequestItems(

                        approval.getRequest()
                                .getRequestItems()
                                .stream()

                                .map(item -> {

                                        RequestItemDTO itemDTO =
                                                new RequestItemDTO();

                                        itemDTO.setRequestItemId(
                                                item.getRequestItemId()
                                        );

                                        itemDTO.setItemId(
                                                item.getItem()
                                                        .getItemId()
                                        );

                                        itemDTO.setItemName(
                                                item.getItem()
                                                        .getItemName()
                                        );

                                        itemDTO.setItemCode(
                                                item.getItem()
                                                        .getItemCode()
                                        );

                                        itemDTO.setRequestedQuantity(
                                                item.getRequestedQuantity()
                                        );

                                        itemDTO.setApprovedQuantity(
                                                item.getApprovedQuantity()
                                        );

                                        itemDTO.setSelected(true);

                                        return itemDTO;
                                })

                                .toList()
                );

                return dto;
        }
}