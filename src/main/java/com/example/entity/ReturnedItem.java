package com.example.entity;

import com.example.entity.base.BaseEntity;
import com.example.enums.ReturnCondition;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "returned_items")
public class ReturnedItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "returned_item_id")
    private Long returnedItemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issued_item_id")
    private IssuedItem issuedItem;

    @Column(name = "return_reference_number")
    private String returnReferenceNumber;

    @Column(name = "returned_quantity")
    private Integer returnedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "return_condition")
    private ReturnCondition returnCondition;

    @Column(name = "return_remarks")
    private String returnRemarks;

    @Column(name = "returned_date")
    private LocalDateTime returnedDate;

    public ReturnedItem() {
    }

    

    public ReturnedItem(Long returnedItemId, IssuedItem issuedItem, String returnReferenceNumber,
            Integer returnedQuantity, ReturnCondition returnCondition, String returnRemarks,
            LocalDateTime returnedDate) {
        this.returnedItemId = returnedItemId;
        this.issuedItem = issuedItem;
        this.returnReferenceNumber = returnReferenceNumber;
        this.returnedQuantity = returnedQuantity;
        this.returnCondition = returnCondition;
        this.returnRemarks = returnRemarks;
        this.returnedDate = returnedDate;
    }



    public Long getReturnedItemId() {
        return returnedItemId;
    }

    public void setReturnedItemId(Long returnedItemId) {
        this.returnedItemId = returnedItemId;
    }

    public IssuedItem getIssuedItem() {
        return issuedItem;
    }

    public void setIssuedItem(IssuedItem issuedItem) {
        this.issuedItem = issuedItem;
    }

    public Integer getReturnedQuantity() {
        return returnedQuantity;
    }

    public void setReturnedQuantity(Integer returnedQuantity) {
        this.returnedQuantity = returnedQuantity;
    }

    public ReturnCondition getReturnCondition() {
        return returnCondition;
    }

    public void setReturnCondition(ReturnCondition returnCondition) {
        this.returnCondition = returnCondition;
    }

    public String getReturnRemarks() {
        return returnRemarks;
    }

    public void setReturnRemarks(String returnRemarks) {
        this.returnRemarks = returnRemarks;
    }

    public LocalDateTime getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDateTime returnedDate) {
        this.returnedDate = returnedDate;
    }



    public String getReturnReferenceNumber() {
        return returnReferenceNumber;
    }



    public void setReturnReferenceNumber(String returnReferenceNumber) {
        this.returnReferenceNumber = returnReferenceNumber;
    }

    
}