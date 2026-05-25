package com.example.dto;

import com.example.enums.ApprovalRole;
import com.example.enums.ApprovalStatus;

public class RequestApprovalDTO {

    private Long approvalId;

    private Long requestId;

    private String requestNumber;

    private Integer approvalOrder;

    private ApprovalRole approvalRole;

    private ApprovalStatus approvalStatus;

    private Boolean isCurrentLevel;

    private String comments;

    public RequestApprovalDTO() {
    }

    public RequestApprovalDTO(Long approvalId, Long requestId, String requestNumber, Integer approvalOrder,
            ApprovalRole approvalRole, ApprovalStatus approvalStatus, Boolean isCurrentLevel, String comments) {
        this.approvalId = approvalId;
        this.requestId = requestId;
        this.requestNumber = requestNumber;
        this.approvalOrder = approvalOrder;
        this.approvalRole = approvalRole;
        this.approvalStatus = approvalStatus;
        this.isCurrentLevel = isCurrentLevel;
        this.comments = comments;
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Integer getApprovalOrder() {
        return approvalOrder;
    }

    public void setApprovalOrder(Integer approvalOrder) {
        this.approvalOrder = approvalOrder;
    }

    public ApprovalRole getApprovalRole() {
        return approvalRole;
    }

    public void setApprovalRole(ApprovalRole approvalRole) {
        this.approvalRole = approvalRole;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Boolean getIsCurrentLevel() {
        return isCurrentLevel;
    }

    public void setIsCurrentLevel(Boolean isCurrentLevel) {
        this.isCurrentLevel = isCurrentLevel;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    
}