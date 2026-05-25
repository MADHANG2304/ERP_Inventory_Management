package com.example.dto;

import com.example.enums.ApprovalType;
import com.example.enums.ItemStatus;
import com.example.enums.UnitType;

public class InventoryItemDTO {
    private Long itemId;

    private Long categoryId;

    private String categoryName;

    private String itemName;

    private String itemCode;

    private String description;

    private Boolean isReusable = false;

    private Boolean allowReturn = false;

    private ApprovalType approvalType;

    private Integer minimumStock;

    private UnitType unitType;

    private ItemStatus status;

    public InventoryItemDTO() {
    }

    public InventoryItemDTO(Long itemId, Long categoryId, String categoryName, String itemName, String itemCode,
            String description, Boolean isReusable, Boolean allowReturn, ApprovalType approvalType,
            Integer minimumStock, UnitType unitType, ItemStatus status) {
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.description = description;
        this.isReusable = isReusable;
        this.allowReturn = allowReturn;
        this.approvalType = approvalType;
        this.minimumStock = minimumStock;
        this.unitType = unitType;
        this.status = status;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsReusable() {
        return isReusable;
    }

    public void setIsReusable(Boolean isReusable) {
        this.isReusable = isReusable;
    }

    public Boolean getAllowReturn() {
        return allowReturn;
    }

    public void setAllowReturn(Boolean allowReturn) {
        this.allowReturn = allowReturn;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    
}
