package com.example.dto;

import com.example.enums.ApprovalStatus;
import com.example.enums.ApprovalRole;

public class ApprovalFilterDTO {

    private String requestNumber;

    private ApprovalStatus approvalStatus;

    private ApprovalRole approvalRole;

    private Boolean currentLevel;

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(
            String requestNumber
    ) {
        this.requestNumber = requestNumber;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(
            ApprovalStatus approvalStatus
    ) {
        this.approvalStatus = approvalStatus;
    }

    public ApprovalRole getApprovalRole() {
        return approvalRole;
    }

    public void setApprovalRole(
            ApprovalRole approvalRole
    ) {
        this.approvalRole = approvalRole;
    }

    public Boolean getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(
            Boolean currentLevel
    ) {
        this.currentLevel = currentLevel;
    }
}