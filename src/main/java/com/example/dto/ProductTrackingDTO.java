package com.example.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProductTrackingDTO {

    private Long assetItemId;

    private String assetReferenceNumber;

    private String itemName;

    private String itemCode;

    private String modelNumber;

    private LocalDate purchaseDate;

    private String purchasePrice;

    private String assetStatus;

    private String currentHolder;

    private String issuedBy;

    private String issueReferenceNumber;

    private LocalDateTime lastIssuedDate;

    private LocalDateTime lastReturnedDate;

    private String latestReturnCondition;

    public ProductTrackingDTO() {
    }

    public Long getAssetItemId() {
        return assetItemId;
    }

    public void setAssetItemId(Long assetItemId) {
        this.assetItemId = assetItemId;
    }

    public String getAssetReferenceNumber() {
        return assetReferenceNumber;
    }

    public void setAssetReferenceNumber(String assetReferenceNumber) {
        this.assetReferenceNumber = assetReferenceNumber;
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

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(String assetStatus) {
        this.assetStatus = assetStatus;
    }

    public String getCurrentHolder() {
        return currentHolder;
    }

    public void setCurrentHolder(String currentHolder) {
        this.currentHolder = currentHolder;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getIssueReferenceNumber() {
        return issueReferenceNumber;
    }

    public void setIssueReferenceNumber(String issueReferenceNumber) {
        this.issueReferenceNumber = issueReferenceNumber;
    }

    public LocalDateTime getLastIssuedDate() {
        return lastIssuedDate;
    }

    public void setLastIssuedDate(LocalDateTime lastIssuedDate) {
        this.lastIssuedDate = lastIssuedDate;
    }

    public LocalDateTime getLastReturnedDate() {
        return lastReturnedDate;
    }

    public void setLastReturnedDate(LocalDateTime lastReturnedDate) {
        this.lastReturnedDate = lastReturnedDate;
    }

    public String getLatestReturnCondition() {
        return latestReturnCondition;
    }

    public void setLatestReturnCondition(String latestReturnCondition) {
        this.latestReturnCondition = latestReturnCondition;
    }
}