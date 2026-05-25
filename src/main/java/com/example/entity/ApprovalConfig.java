package com.example.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.entity.base.BaseEntity;
import com.example.enums.RequestType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "approval_config")
public class ApprovalConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Long configId;

    @Column(name = "config_name",
            nullable = false,
            unique = true)
    private String configName;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private RequestType requestType;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(
            mappedBy = "approvalConfig",
            fetch= FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ApprovalConfigLevel> levels =
            new ArrayList<>();

    public ApprovalConfig() {
    }

    public ApprovalConfig(Long configId, String configName, RequestType requestType, Boolean isActive,
            List<ApprovalConfigLevel> levels) {
        this.configId = configId;
        this.configName = configName;
        this.requestType = requestType;
        this.isActive = isActive;
        this.levels = levels;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<ApprovalConfigLevel> getLevels() {
        return levels;
    }

    public void setLevels(List<ApprovalConfigLevel> levels) {
        this.levels = levels;
    }

    
    
}