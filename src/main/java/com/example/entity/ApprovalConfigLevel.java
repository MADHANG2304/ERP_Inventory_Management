package com.example.entity;

import com.example.entity.base.BaseEntity;
import com.example.enums.ApprovalRole;

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
@Table(name = "approval_config_levels")
public class ApprovalConfigLevel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_id")
    private Long levelId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "config_id")
    private ApprovalConfig approvalConfig;

    @Column(name = "approval_order")
    private Integer approvalOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_role")
    private ApprovalRole approvalRole;

    public ApprovalConfigLevel() {
    }

    public ApprovalConfigLevel(Long levelId, ApprovalConfig approvalConfig, Integer approvalOrder,
            ApprovalRole approvalRole) {
        this.levelId = levelId;
        this.approvalConfig = approvalConfig;
        this.approvalOrder = approvalOrder;
        this.approvalRole = approvalRole;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public ApprovalConfig getApprovalConfig() {
        return approvalConfig;
    }

    public void setApprovalConfig(ApprovalConfig approvalConfig) {
        this.approvalConfig = approvalConfig;
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