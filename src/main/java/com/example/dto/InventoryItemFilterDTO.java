package com.example.dto;

import com.example.enums.ApprovalType;
import com.example.enums.ItemStatus;

public class InventoryItemFilterDTO {

    private Long categoryId;

    private String itemCode;

    private ApprovalType approvalType;

    private ItemStatus status;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(
            Long categoryId
    ) {
        this.categoryId = categoryId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(
            String itemCode
    ) {
        this.itemCode = itemCode;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(
            ApprovalType approvalType
    ) {
        this.approvalType = approvalType;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(
            ItemStatus status
    ) {
        this.status = status;
    }
}