package com.example.dto;

import java.time.LocalDateTime;

public class IssuedItemDTO {

    private Long issuedItemId;

    private Long requestId;

    private String requestNumber;

    private String employeeName;

    private String itemName;

    private String itemCode;

    private Integer requestedQuantity;

    private Integer issuedQuantity;

    private LocalDateTime issuedDate;

    private String issueReferenceNumber;
    
    private Long requestItemId;

    
    public IssuedItemDTO() {
    }
    
    public IssuedItemDTO(Long issuedItemId, Long requestId, String requestNumber, String employeeName, String itemName,
        String itemCode, Integer requestedQuantity, Integer issuedQuantity, LocalDateTime issuedDate,
        String issueReferenceNumber) {
        this.issuedItemId = issuedItemId;
        this.requestId = requestId;
        this.requestNumber = requestNumber;
        this.employeeName = employeeName;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.requestedQuantity = requestedQuantity;
        this.issuedQuantity = issuedQuantity;
        this.issuedDate = issuedDate;
        this.issueReferenceNumber = issueReferenceNumber;
    }
    
    public Long getRequestItemId() {
        return requestItemId;
    }

    public void setRequestItemId(Long requestItemId) {
        this.requestItemId = requestItemId;
    }
    
    public Long getIssuedItemId() {
        return issuedItemId;
    }
    
    public void setIssuedItemId(Long issuedItemId) {
        this.issuedItemId = issuedItemId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public Integer getIssuedQuantity() {
        return issuedQuantity;
    }

    public void setIssuedQuantity(Integer issuedQuantity) {
        this.issuedQuantity = issuedQuantity;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getIssueReferenceNumber() {
        return issueReferenceNumber;
    }

    public void setIssueReferenceNumber(String issueReferenceNumber) {
        this.issueReferenceNumber = issueReferenceNumber;
    }


    
}