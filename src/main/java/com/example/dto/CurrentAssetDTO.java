package com.example.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CurrentAssetDTO {

    private Long issuedItemId;

    private String itemName;

    private String itemCode;

    private String assetReferenceNumber;

    private String modelNumber;

    private LocalDate purchaseDate;

    private LocalDateTime issuedDate;

    private String issueReferenceNumber;

    public Long getIssuedItemId() {
        return issuedItemId;
    }

    public void setIssuedItemId(Long issuedItemId) {
        this.issuedItemId = issuedItemId;
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

    public String getAssetReferenceNumber() {
        return assetReferenceNumber;
    }

    public void setAssetReferenceNumber(String assetReferenceNumber) {
        this.assetReferenceNumber = assetReferenceNumber;
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