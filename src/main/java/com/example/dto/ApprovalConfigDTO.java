package com.example.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.enums.RequestType;

public class ApprovalConfigDTO {

    private Long configId;

    private String configName;

    private RequestType requestType;

    private Boolean isActive = true;

    private List<ApprovalConfigLevelDTO> levels =
            new ArrayList<>();

    public ApprovalConfigDTO() {
    }

    public ApprovalConfigDTO(Long configId, String configName, RequestType requestType, Boolean isActive,
            List<ApprovalConfigLevelDTO> levels) {
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

    public List<ApprovalConfigLevelDTO> getLevels() {
        return levels;
    }

    public void setLevels(List<ApprovalConfigLevelDTO> levels) {
        this.levels = levels;
    }

    
}