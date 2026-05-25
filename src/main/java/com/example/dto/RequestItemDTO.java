package com.example.dto;


public class RequestItemDTO {

    private Long requestItemId;

    private Long itemId;

    private String itemName;

    private String itemCode;

    private Integer requestedQuantity;

    private Integer approvedQuantity;

    public RequestItemDTO() {
    }

    public RequestItemDTO(Long requestItemId, Long itemId, String itemName, String itemCode, Integer requestedQuantity,
            Integer approvedQuantity) {
        this.requestItemId = requestItemId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.requestedQuantity = requestedQuantity;
        this.approvedQuantity = approvedQuantity;
    }

    public Long getRequestItemId() {
        return requestItemId;
    }

    public void setRequestItemId(Long requestItemId) {
        this.requestItemId = requestItemId;
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

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public Integer getApprovedQuantity() {
        return approvedQuantity;
    }

    public void setApprovedQuantity(Integer approvedQuantity) {
        this.approvedQuantity = approvedQuantity;
    }

    
}