package com.example.entity;

import com.example.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="inventory_stock")
public class InventoryStock extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="stock_id")
    private Long stockId;

    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="item_id")
    private InventoryItem item;

    @Column(name="available_quantity")
    private Integer availableQuantity;

    @Column(name="issued_quantity")
    private Integer issuedQuantity;

    @Column(name="damaged_quantity")
    private Integer damagedQuantity;

    public InventoryStock() {
    }

    public InventoryStock(Long stockId, InventoryItem item, Integer availableQuantity, Integer issuedQuantity,
            Integer damagedQuantity) {
        this.stockId = stockId;
        this.item = item;
        this.availableQuantity = availableQuantity;
        this.issuedQuantity = issuedQuantity;
        this.damagedQuantity = damagedQuantity;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
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

    
}
