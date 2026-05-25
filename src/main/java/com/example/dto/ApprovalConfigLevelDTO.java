package com.example.dto;

import com.example.enums.ApprovalRole;

public class ApprovalConfigLevelDTO {

    private Long levelId;

    private Integer approvalOrder;

    private ApprovalRole approvalRole;

    public ApprovalConfigLevelDTO() {
    }

    public ApprovalConfigLevelDTO(Long levelId, Integer approvalOrder, ApprovalRole approvalRole) {
        this.levelId = levelId;
        this.approvalOrder = approvalOrder;
        this.approvalRole = approvalRole;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
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


    
}