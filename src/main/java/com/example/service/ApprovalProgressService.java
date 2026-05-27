package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dto.ApprovalProgressDTO;
import com.example.entity.RequestApproval;
import com.example.repository.RequestApprovalRepository;

@Service
public class ApprovalProgressService {

    private final RequestApprovalRepository requestApprovalRepository;

    public ApprovalProgressService(
            RequestApprovalRepository requestApprovalRepository
    ) {

        this.requestApprovalRepository =
                requestApprovalRepository;
    }

    public List<ApprovalProgressDTO>
    getApprovalProgress(Long requestId) {

        List<RequestApproval> approvals =
                requestApprovalRepository
                        .findAll()
                        .stream()

                        .filter(approval ->

                                approval.getRequest()
                                        .getRequestId()
                                        .equals(requestId)

                        )

                        .sorted((a, b) ->

                                a.getApprovalOrder()
                                        .compareTo(
                                                b.getApprovalOrder()
                                        )
                        )

                        .collect(Collectors.toList());

        return approvals.stream()
                .map(approval -> {

                    ApprovalProgressDTO dto = new ApprovalProgressDTO();

                    dto.setRequestNumber(
                            approval.getRequest()
                                    .getRequestNumber()
                    );

                    dto.setApprovalLevel(
                            approval.getApprovalOrder()
                    );

                    dto.setApprovalRole(
                            approval.getApprovalRole()
                    );

                    dto.setApprovalStatus(
                            approval.getApprovalStatus()
                    );

                    dto.setCurrentLevel(
                            approval.getIsCurrentLevel()
                    );

                    dto.setActionDate(
                            approval.getModifiedAt()
                    );

                    return dto;
                })
                .collect(Collectors.toList());
    }
}