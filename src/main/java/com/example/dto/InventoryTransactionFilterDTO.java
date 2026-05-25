package com.example.dto;

import com.example.enums.ReferenceType;
import com.example.enums.TransactionType;

public class InventoryTransactionFilterDTO {

    private String itemName;

    private TransactionType transactionType;

    private ReferenceType referenceType;

    private String referenceNumber;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(
            TransactionType transactionType
    ) {
        this.transactionType = transactionType;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(
            ReferenceType referenceType
    ) {
        this.referenceType = referenceType;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(
            String referenceNumber
    ) {
        this.referenceNumber = referenceNumber;
    }
}