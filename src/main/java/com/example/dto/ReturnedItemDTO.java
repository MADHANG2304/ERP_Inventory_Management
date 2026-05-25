package com.example.dto;

import com.example.enums.IssueStatus;
import com.example.enums.ReturnCondition;

import java.time.LocalDateTime;

public class ReturnedItemDTO {

    private Long issuedItemId;

    private String issueReferenceNumber;

    private String employeeName;

    private String itemName;

    private String itemCode;

    private Integer issuedQuantity;

    private Integer returnQuantity;

    private ReturnCondition returnCondition;

    private String returnRemarks;

    private LocalDateTime returnedDate;

    private IssueStatus issueStatus;

    public ReturnedItemDTO() {
    }

    public ReturnedItemDTO(Long issuedItemId, String issueReferenceNumber, String employeeName, String itemName,
            String itemCode, Integer issuedQuantity, Integer returnQuantity, ReturnCondition returnCondition,
            String returnRemarks, LocalDateTime returnedDate, IssueStatus issueStatus) {
        this.issuedItemId = issuedItemId;
        this.issueReferenceNumber = issueReferenceNumber;
        this.employeeName = employeeName;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.issuedQuantity = issuedQuantity;
        this.returnQuantity = returnQuantity;
        this.returnCondition = returnCondition;
        this.returnRemarks = returnRemarks;
        this.returnedDate = returnedDate;
        this.issueStatus = issueStatus;
    }

    public Long getIssuedItemId() {
        return issuedItemId;
    }

    public void setIssuedItemId(Long issuedItemId) {
        this.issuedItemId = issuedItemId;
    }

    public String getIssueReferenceNumber() {
        return issueReferenceNumber;
    }

    public void setIssueReferenceNumber(String issueReferenceNumber) {
        this.issueReferenceNumber = issueReferenceNumber;
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

    public Integer getIssuedQuantity() {
        return issuedQuantity;
    }

    public void setIssuedQuantity(Integer issuedQuantity) {
        this.issuedQuantity = issuedQuantity;
    }

    public Integer getReturnQuantity() {
        return returnQuantity;
    }

    public void setReturnQuantity(Integer returnQuantity) {
        this.returnQuantity = returnQuantity;
    }

    public ReturnCondition getReturnCondition() {
        return returnCondition;
    }

    public void setReturnCondition(ReturnCondition returnCondition) {
        this.returnCondition = returnCondition;
    }

    public String getReturnRemarks() {
        return returnRemarks;
    }

    public void setReturnRemarks(String returnRemarks) {
        this.returnRemarks = returnRemarks;
    }

    public LocalDateTime getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDateTime returnedDate) {
        this.returnedDate = returnedDate;
    }

    public IssueStatus getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(IssueStatus issueStatus) {
        this.issueStatus = issueStatus;
    }

    
}