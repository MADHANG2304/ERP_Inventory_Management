package com.example.dto;

import java.time.LocalDateTime;

import com.example.enums.ApprovalStatus;
import com.example.enums.ApprovalRole;

public class ApprovalProgressDTO {

    private String requestNumber;

    private Integer approvalLevel;

    private ApprovalRole approvalRole;

    private ApprovalStatus approvalStatus;

    private Boolean currentLevel;

    private LocalDateTime actionDate;

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
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

    public Boolean getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Boolean currentLevel) {
        this.currentLevel = currentLevel;
    }

    public LocalDateTime getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDateTime actionDate) {
        this.actionDate = actionDate;
    }
}