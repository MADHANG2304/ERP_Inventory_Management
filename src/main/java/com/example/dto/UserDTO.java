package com.example.dto;

public class UserDTO {

    private Long userId;

    private Long employeeId;

    private String employeeName;

    private String email;

    private Long roleId;

    private String roleName;

    private String username;

    private String generatedPassword;

    private Boolean isActive;

    public UserDTO() {
    }

    public UserDTO(Long userId, Long employeeId, String employeeName, String email, Long roleId, String roleName,
            String username, String generatedPassword, Boolean isActive) {
        this.userId = userId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
        this.username = username;
        this.generatedPassword = generatedPassword;
        this.isActive = isActive;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGeneratedPassword() {
        return generatedPassword;
    }

    public void setGeneratedPassword(String generatedPassword) {
        this.generatedPassword = generatedPassword;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    
}