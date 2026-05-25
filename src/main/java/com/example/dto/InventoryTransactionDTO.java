package com.example.dto;

import java.time.LocalDateTime;

import com.example.enums.TransactionType;

import jakarta.persistence.Column;

import com.example.enums.ReferenceType;

public class InventoryTransactionDTO {

    private Long transactionId;

    private Long itemId;

    private String itemName;

    private TransactionType transactionType;

    private ReferenceType referenceType;

    private String referenceNumber;

    private Integer quantity;

    private String remarks;

    private LocalDateTime transactionDate;

    public InventoryTransactionDTO() {
    }

    public InventoryTransactionDTO(Long transactionId, Long itemId, String itemName, TransactionType transactionType,
            ReferenceType referenceType, String referenceNumber, Integer quantity, String remarks,
            LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.transactionType = transactionType;
        this.referenceType = referenceType;
        this.referenceNumber = referenceNumber;
        this.quantity = quantity;
        this.remarks = remarks;
        this.transactionDate = transactionDate;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    
    
}