package com.example.entity;

import com.example.entity.base.BaseEntity;
import com.example.enums.ApprovalRole;
import com.example.enums.ApprovalStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "request_approvals")
public class RequestApproval extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    private InventoryRequest request;

    @Column(name = "approval_order")
    private Integer approvalOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_role")
    private ApprovalRole approvalRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;

    @Column(name = "is_current_level")
    private Boolean isCurrentLevel = false;

    @Column(name = "comments")
    private String comments;

    public RequestApproval() {
    }

    public RequestApproval(Long approvalId, InventoryRequest request, Integer approvalOrder, ApprovalRole approvalRole,
            ApprovalStatus approvalStatus, Boolean isCurrentLevel, String comments) {
        this.approvalId = approvalId;
        this.request = request;
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

    public InventoryRequest getRequest() {
        return request;
    }

    public void setRequest(InventoryRequest request) {
        this.request = request;
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