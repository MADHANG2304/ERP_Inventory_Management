package com.example.dto;

public class EmployeeDTO {

    private Long employeeId;

    private Long departmentId;

    private String departmentName;

    private Long designationId; 

    private String designationName;

    private String employeeName;

    private String mobileNumber;

    private String email;

    private String gender;

    private String state;

    private String city;

    private Boolean isActive = true;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Long employeeId, Long departmentId, String departmentName, Long designationId,
            String designationName, String employeeName, String mobileNumber, String email, String gender, String state,
            String city, Boolean isActive) {
        this.employeeId = employeeId;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.designationId = designationId;
        this.designationName = designationName;
        this.employeeName = employeeName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.gender = gender;
        this.state = state;
        this.city = city;
        this.isActive = isActive;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    
}