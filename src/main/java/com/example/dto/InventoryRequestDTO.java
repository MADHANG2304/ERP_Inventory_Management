package com.example.dto;

import com.example.enums.RequestStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryRequestDTO {

    private Long requestId;

    private Long employeeId;

    private String employeeName;

    private String requestNumber;

    private RequestStatus requestStatus;

    private String remarks;

    private LocalDateTime requestDate;

    private List<RequestItemDTO> requestItems = new ArrayList<>();

    public InventoryRequestDTO() {
    }

    public InventoryRequestDTO(Long requestId, Long employeeId, String employeeName, String requestNumber,
            RequestStatus requestStatus, String remarks, LocalDateTime requestDate, List<RequestItemDTO> requestItems) {
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.requestNumber = requestNumber;
        this.requestStatus = requestStatus;
        this.remarks = remarks;
        this.requestDate = requestDate;
        this.requestItems = requestItems;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public List<RequestItemDTO> getRequestItems() {
        return requestItems;
    }

    public void setRequestItems(List<RequestItemDTO> requestItems) {
        this.requestItems = requestItems;
    }

    
}