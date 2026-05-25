package com.example.dto;

public class DesignationDTO {
    
    private Long designationId;

    private String designationName;
    
    private String designationCode;

    private Boolean isActive;

    

    public DesignationDTO(Long designationId, String designationName, String designationCode, Boolean isActive) {
        this.designationId = designationId;
        this.designationName = designationName;
        this.designationCode = designationCode;
        this.isActive = isActive;
    }

    public DesignationDTO() {
    }

    public Long getDesignationId() {
        return designationId;
    }

    public void setDesignationId(Long designationId) {
        this.designationId = designationId;
    }

    public String getDesignationName() {
        return designationName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }

    public String getDesignationCode() {
        return designationCode;
    }

    public void setDesignationCode(String designationCode) {
        this.designationCode = designationCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    
}
