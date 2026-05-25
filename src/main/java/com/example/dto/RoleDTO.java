package com.example.dto;

public class RoleDTO {
    
    private Long roleId;

    private String roleName;

    private String description;

    private Boolean isActive;

    public RoleDTO() {
    }

    public RoleDTO(Long roleId, String roleName, String description, Boolean isActive) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
        this.isActive = isActive;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    
}
