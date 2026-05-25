package com.example.entity;

import com.example.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "request_items")
public class RequestItems extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_item_id")
    private Long requestItemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    private InventoryRequest request;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private InventoryItem item;

    @Column(name = "requested_quantity")
    private Integer requestedQuantity;

    @Column(name = "approved_quantity")
    private Integer approvedQuantity = 0;

    public RequestItems() {
    }

    public RequestItems(Long requestItemId, InventoryRequest request, InventoryItem item, Integer requestedQuantity,
            Integer approvedQuantity) {
        this.requestItemId = requestItemId;
        this.request = request;
        this.item = item;
        this.requestedQuantity = requestedQuantity;
        this.approvedQuantity = approvedQuantity;
    }

    public Long getRequestItemId() {
        return requestItemId;
    }

    public void setRequestItemId(Long requestItemId) {
        this.requestItemId = requestItemId;
    }

    public InventoryRequest getRequest() {
        return request;
    }

    public void setRequest(InventoryRequest request) {
        this.request = request;
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
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