package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dto.ApprovalProgressDTO;
import com.example.dto.ApprovalProgressItemDTO;
import com.example.entity.InventoryRequest;
import com.example.entity.RequestApproval;
import com.example.repository.InventoryRequestRepository;
import com.example.repository.IssuedItemRepository;
import com.example.repository.RequestApprovalRepository;

@Service
public class ApprovalProgressService {

        private final RequestApprovalRepository requestApprovalRepository;

        private final InventoryRequestRepository inventoryRequestRepository;

        private final IssuedItemRepository issuedItemRepository;

        public ApprovalProgressService(RequestApprovalRepository requestApprovalRepository,InventoryRequestRepository inventoryRequestRepository, IssuedItemRepository issuedItemRepository){
                this.requestApprovalRepository = requestApprovalRepository;
                this.inventoryRequestRepository = inventoryRequestRepository;
                this.issuedItemRepository = issuedItemRepository;
        }

        public List<ApprovalProgressDTO> getApprovalProgress(Long requestId) {

                List<RequestApproval> approvals = requestApprovalRepository
                                .findAll()
                                .stream()
                                .filter(approval ->
                                        approval.getRequest().getRequestId().equals(requestId)
                                )
                                .sorted((a, b) ->
                                        a.getApprovalOrder().compareTo(b.getApprovalOrder())
                                )
                                .collect(Collectors.toList());

                return approvals.stream()
                        .map(approval -> {
                                ApprovalProgressDTO dto = new ApprovalProgressDTO();

                                dto.setRequestNumber(approval.getRequest().getRequestNumber());

                                dto.setApprovalLevel(approval.getApprovalOrder());

                                dto.setApprovalRole(approval.getApprovalRole());

                                dto.setApprovalStatus(approval.getApprovalStatus());

                                dto.setCurrentLevel(approval.getIsCurrentLevel());
                                
                                dto.setActionDate(approval.getModifiedAt());

                                return dto;
                        })
                        .collect(Collectors.toList());
        }

        public List<ApprovalProgressItemDTO> getRequestItemSummary(Long requestId) {

                InventoryRequest request = inventoryRequestRepository
                                .findById(requestId)
                                .orElseThrow(() -> new RuntimeException("Request not found"));

                return request.getRequestItems()
                        .stream()
                        .map(item -> {
                                ApprovalProgressItemDTO dto = new ApprovalProgressItemDTO();

                                Integer issuedQty = issuedItemRepository.getIssuedQuantityForRequestItem(item.getRequestItemId());

                                if (issuedQty == null) {
                                        issuedQty = 0;
                                }

                                dto.setItemName(item.getItem().getItemName());

                                dto.setItemCode(item.getItem().getItemCode());

                                dto.setRequestedQuantity(item.getRequestedQuantity());

                                dto.setApprovedQuantity(item.getApprovedQuantity());

                                dto.setIssuedQuantity(issuedQty);

                                dto.setRemainingQuantity(Math.max(item.getApprovedQuantity() - issuedQty,0));

                                return dto;
                        })
                        .collect(Collectors.toList());
        }
}