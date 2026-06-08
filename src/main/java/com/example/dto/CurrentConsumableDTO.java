package com.example.dto;

public class CurrentConsumableDTO {

    private Long itemId;

    private String itemName;

    private String itemCode;

    private Integer totalIssued;

    private Integer totalReturned;

    private Integer currentBalance;

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

    public Integer getTotalIssued() {
        return totalIssued;
    }

    public void setTotalIssued(Integer totalIssued) {
        this.totalIssued = totalIssued;
    }

    public Integer getTotalReturned() {
        return totalReturned;
    }

    public void setTotalReturned(Integer totalReturned) {
        this.totalReturned = totalReturned;
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
    }
}