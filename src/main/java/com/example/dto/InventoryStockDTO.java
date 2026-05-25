package com.example.dto;

public class InventoryStockDTO {

    private Long stockId;

    private Long itemId;

    private String itemName;

    private String itemCode;

    private Integer availableQuantity;

    private Integer issuedQuantity;

    private Integer damagedQuantity;

    private Boolean lowStock;

    public InventoryStockDTO() {
    }

    public InventoryStockDTO(Long stockId, Long itemId, String itemName, String itemCode, Integer availableQuantity,
            Integer issuedQuantity, Integer damagedQuantity, Boolean lowStock) {
        this.stockId = stockId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.availableQuantity = availableQuantity;
        this.issuedQuantity = issuedQuantity;
        this.damagedQuantity = damagedQuantity;
        this.lowStock = lowStock;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
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

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getIssuedQuantity() {
        return issuedQuantity;
    }

    public void setIssuedQuantity(Integer issuedQuantity) {
        this.issuedQuantity = issuedQuantity;
    }

    public Integer getDamagedQuantity() {
        return damagedQuantity;
    }

    public void setDamagedQuantity(Integer damagedQuantity) {
        this.damagedQuantity = damagedQuantity;
    }

    public Boolean getLowStock() {
        return lowStock;
    }

    public void setLowStock(Boolean lowStock) {
        this.lowStock = lowStock;
    }

    
}