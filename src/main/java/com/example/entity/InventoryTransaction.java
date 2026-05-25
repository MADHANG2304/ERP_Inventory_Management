package com.example.entity;

import java.time.LocalDateTime;

import com.example.entity.base.BaseEntity;
import com.example.enums.ReferenceType;
import com.example.enums.TransactionType;

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
@Table(name="inventory_transactions")
public class InventoryTransaction extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="transaction_id")
    private Long transactionId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="item_id")
    private InventoryItem item;

    @Enumerated(EnumType.STRING)
    @Column(name="transaction_type")
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name="reference_type")
    private ReferenceType referenceType;

    @Column(name = "reference_number")
    private String referenceNumber;

    private Integer quantity;

    @Column(name="transaction_date")
    private LocalDateTime transactionDate;

    private String remarks;

    public InventoryTransaction() {
    }

    public InventoryTransaction(Long transactionId, InventoryItem item, TransactionType transactionType,
            ReferenceType referenceType, String referenceNumber, Integer quantity, LocalDateTime transactionDate,
            String remarks) {
        this.transactionId = transactionId;
        this.item = item;
        this.transactionType = transactionType;
        this.referenceType = referenceType;
        this.referenceNumber = referenceNumber;
        this.quantity = quantity;
        this.transactionDate = transactionDate;
        this.remarks = remarks;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    
    
}
