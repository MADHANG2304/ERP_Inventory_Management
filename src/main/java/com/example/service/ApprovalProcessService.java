package com.example.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.dto.ApprovalFilterDTO;
import com.example.dto.RequestApprovalDTO;
import com.example.entity.InventoryRequest;
import com.example.entity.RequestApproval;
import com.example.entity.User;
import com.example.enums.ApprovalStatus;
import com.example.enums.RequestStatus;
import com.example.repository.InventoryRequestRepository;
import com.example.repository.RequestApprovalRepository;
import com.example.repository.UserRepository;
import com.example.specification.ApprovalSpecification;

@Service
public class ApprovalProcessService {

    private final RequestApprovalRepository
            requestApprovalRepository;

    private final InventoryRequestRepository
            inventoryRequestRepository;

    private final UserRepository userRepository;

    private final AuditLogService auditLogService;

    public ApprovalProcessService(
            RequestApprovalRepository requestApprovalRepository,
            InventoryRequestRepository inventoryRequestRepository,
            UserRepository userRepository,
            AuditLogService auditLogService
    ) {

        this.requestApprovalRepository =
                requestApprovalRepository;

        this.inventoryRequestRepository =
                inventoryRequestRepository;

        this.userRepository =
                userRepository;

        this.auditLogService = auditLogService;
    }

    public List<RequestApprovalDTO> getPendingApprovals(String username) {

        User user =
                userRepository.findAll()
                        .stream()
                        .filter(u ->
                                u.getUsername().equals(username)
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        String roleName = user.getRoles().getRoleName();

        return requestApprovalRepository
                .findAll()
                .stream()

                .filter(approval ->

                        Boolean.TRUE.equals(
                                approval.getIsCurrentLevel()
                        )

                        &&

                        approval.getApprovalStatus()
                                == ApprovalStatus.PENDING

                        &&

                        // approval.getApprovalRole()
                        //         .name()
                        //         .equals(roleName)

                        approval.getApprover() != null

                        &&

                        user.getEmployee() != null

                        &&

                        approval.getApprover().getEmployeeId() != null

                        &&

                        approval.getApprover()
                                .getEmployeeId()
                                .equals(
                                        user.getEmployee()
                                                .getEmployeeId()
                                )
                )

                .sorted(
                        Comparator.comparing(
                                approval -> approval
                                        .getRequest()
                                        .getRequestDate()
                        )
                )

                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void approveRequest(
        Long approvalId,
        String comments
        ) {

        RequestApproval currentApproval =
                requestApprovalRepository
                        .findById(approvalId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Approval not found"
                                )
                        );

        if(!Boolean.TRUE.equals(
                currentApproval.getIsCurrentLevel()
        )) { 

                throw new RuntimeException(
                        "Invalid approval level"
                );
        }

        currentApproval.setApprovalStatus(
                ApprovalStatus.APPROVED
        );

        currentApproval.setComments(
                comments
        );

        currentApproval.setIsCurrentLevel(
                false
        );

        requestApprovalRepository.save(
                currentApproval
        );

        InventoryRequest request =
                currentApproval.getRequest();

        List<RequestApproval> approvals =
                request.getApprovals()
                        .stream()

                        .sorted(
                                Comparator.comparing(
                                        RequestApproval
                                                ::getApprovalOrder
                                )
                        )

                        .toList();

        RequestApproval nextApproval = approvals
                .stream()

                .filter(approval ->

                        approval.getApprovalOrder()
                                == currentApproval.getApprovalOrder() + 1

                        &&

                        approval.getApprovalStatus()
                                == ApprovalStatus.PENDING
                )

                .findFirst()

                .orElse(null);

    if(nextApproval != null) {

        nextApproval.setIsCurrentLevel(
                    true
            );

            requestApprovalRepository.save(
                    nextApproval
            );

    } else {

        request.setRequestStatus(
                RequestStatus.APPROVED
        );

        inventoryRequestRepository.save(
                request
        );

        auditLogService.logAction(

                "APPROVAL_MODULE",

                "APPROVE",

                "Approved request : "
                        + request.getRequestNumber()
        );
    }
}
    public void rejectRequest(
            Long approvalId,
            String comments
    ) {

        RequestApproval currentApproval =
                requestApprovalRepository
                        .findById(approvalId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Approval not found"
                                )
                        );

        currentApproval.setApprovalStatus(
                ApprovalStatus.REJECTED
        );

        currentApproval.setComments(
                comments
        );

        currentApproval.setIsCurrentLevel(
                false
        );

        requestApprovalRepository.save(
                currentApproval
        );

        InventoryRequest request =
                currentApproval.getRequest();

        request.setRequestStatus(
                RequestStatus.REJECTED
        );

        inventoryRequestRepository.save(
                request
        );

        auditLogService.logAction(

        "APPROVAL_MODULE",

        "REJECT",

        "Rejected request : "
                + request.getRequestNumber()
);
    }

        public List<RequestApprovalDTO>
                filterApprovals(
                        ApprovalFilterDTO filterDTO
        ) {

                Specification<RequestApproval>
                        specification =

                        ApprovalSpecification
                                .hasRequestNumber(
                                        filterDTO.getRequestNumber()
                                )

                                .and(

                                        ApprovalSpecification
                                                .hasApprovalStatus(
                                                        filterDTO.getApprovalStatus()
                                                )
                                )

                                .and(

                                        ApprovalSpecification
                                                .hasApprovalRole(
                                                        filterDTO.getApprovalRole()
                                                )
                                )

                                .and(

                                        ApprovalSpecification
                                                .hasCurrentLevel(
                                                        filterDTO.getCurrentLevel()
                                                )
                                );

                return requestApprovalRepository
                        .findAll(specification)
                        .stream()
                        .map(this::convertToDTO)
                        .toList();
        }

    private RequestApprovalDTO convertToDTO(
            RequestApproval approval
    ) {

        RequestApprovalDTO dto =
                new RequestApprovalDTO();

        dto.setApprovalId(
                approval.getApprovalId()
        );

        dto.setRequestId(
                approval.getRequest()
                        .getRequestId()
        );

        dto.setRequestNumber(
                approval.getRequest()
                        .getRequestNumber()
        );

        dto.setApprovalOrder(
                approval.getApprovalOrder()
        );

        dto.setApprovalRole(
                approval.getApprovalRole()
        );

        dto.setApprovalStatus(
                approval.getApprovalStatus()
        );

        dto.setIsCurrentLevel(
                approval.getIsCurrentLevel()
        );

        dto.setComments(
                approval.getComments()
        );

        return dto;
    }
}