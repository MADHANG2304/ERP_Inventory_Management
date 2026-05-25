package com.example.entity;

import com.example.entity.base.BaseEntity;
import com.example.enums.ApprovalType;
import com.example.enums.ItemStatus;
import com.example.enums.UnitType;

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
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="inventory_item")
public class InventoryItem extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="item_id")
    private Long itemId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="category_id")
    private InventoryCategory category;

    @NotBlank
    @Column(name="item_name",nullable=false)
    private String itemName;

    @Column(name="item_code",unique=true)
    private String itemCode;

    private String description;

    @Column(name="is_reusable")
    private Boolean isReusable;

    @Column(name = "allow_return")
    private Boolean allowReturn = false;

    @Column(name="minimum_stock")
    private Integer minimumStock;

    @Enumerated(EnumType.STRING)
    @Column(name="approval_type")
    private ApprovalType approvalType;

    @Enumerated(EnumType.STRING)
    @Column(name="unit_type")
    private UnitType unitType;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ItemStatus status;

    public InventoryItem() {
    }

    public InventoryItem(Long itemId, InventoryCategory category, @NotBlank String itemName, String itemCode,
            String description, Boolean isReusable, Boolean allowReturn, Integer minimumStock,
            ApprovalType approvalType, UnitType unitType, ItemStatus status) {
        this.itemId = itemId;
        this.category = category;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.description = description;
        this.isReusable = isReusable;
        this.allowReturn = allowReturn;
        this.minimumStock = minimumStock;
        this.approvalType = approvalType;
        this.unitType = unitType;
        this.status = status;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public InventoryCategory getCategory() {
        return category;
    }

    public void setCategory(InventoryCategory category) {
        this.category = category;
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

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
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
