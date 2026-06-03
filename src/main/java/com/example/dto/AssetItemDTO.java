package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.enums.AssetStatus;

public class AssetItemDTO {

    private Long assetItemId;

    private Long itemId;

    private String itemName;

    private String itemCode;

    private String assetReferenceNumber;

    private String modelNumber;

    private LocalDate purchaseDate;

    private BigDecimal purchasePrice;

    private AssetStatus assetStatus;

    public AssetItemDTO() {
    }

    public Long getAssetItemId() {
        return assetItemId;
    }

    public void setAssetItemId(Long assetItemId) {
        this.assetItemId = assetItemId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public AssetStatus getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(AssetStatus assetStatus) {
        this.assetStatus = assetStatus;
    }
}