package com.example.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.entity.base.BaseEntity;
import com.example.enums.AssetStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "asset_items")
public class AssetItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_item_id")
    private Long assetItemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private InventoryItem item;

    @Column(name = "asset_reference_number",
            unique = true,
            nullable = false)
    private String assetReferenceNumber;

    @Column(name = "model_number")
    private String modelNumber;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_status")
    private AssetStatus assetStatus;

    public AssetItem() {
    }

    public AssetItem(Long assetItemId, InventoryItem item, String assetReferenceNumber, String modelNumber,
            LocalDate purchaseDate, BigDecimal purchasePrice, AssetStatus assetStatus) {
        this.assetItemId = assetItemId;
        this.item = item;
        this.assetReferenceNumber = assetReferenceNumber;
        this.modelNumber = modelNumber;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
        this.assetStatus = assetStatus;
    }

    public Long getAssetItemId() {
        return assetItemId;
    }

    public void setAssetItemId(Long assetItemId) {
        this.assetItemId = assetItemId;
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
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
