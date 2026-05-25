package com.example.entity;

import java.time.LocalDateTime;

import com.example.entity.base.BaseEntity;
import com.example.enums.IssueStatus;

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
@Table(name = "issued_items")
public class IssuedItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issued_item_id")
    private Long issuedItemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    private InventoryRequest request;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_item_id")
    private RequestItems requestItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issued_to_employee_id")
    private Employee issuedToEmployee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issued_by_user_id")
    private User issuedBy;

    @Column(name = "issued_quantity")
    private Integer issuedQuantity;

    @Column(name = "issued_date")
    private LocalDateTime issuedDate;

    @Column(name = "issue_reference_number")
    private String issueReferenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_status")
    private IssueStatus issueStatus;

    public IssuedItem() {
    }

    public IssuedItem(Long issuedItemId, InventoryRequest request, RequestItems requestItem, Employee issuedToEmployee,
            User issuedBy, Integer issuedQuantity, LocalDateTime issuedDate, String issueReferenceNumber,
            IssueStatus issueStatus) {
        this.issuedItemId = issuedItemId;
        this.request = request;
        this.requestItem = requestItem;
        this.issuedToEmployee = issuedToEmployee;
        this.issuedBy = issuedBy;
        this.issuedQuantity = issuedQuantity;
        this.issuedDate = issuedDate;
        this.issueReferenceNumber = issueReferenceNumber;
        this.issueStatus = issueStatus;
    }

    public Long getIssuedItemId() {
        return issuedItemId;
    }

    public void setIssuedItemId(Long issuedItemId) {
        this.issuedItemId = issuedItemId;
    }

    public InventoryRequest getRequest() {
        return request;
    }

    public void setRequest(InventoryRequest request) {
        this.request = request;
    }

    public RequestItems getRequestItem() {
        return requestItem;
    }

    public void setRequestItem(RequestItems requestItem) {
        this.requestItem = requestItem;
    }

    public Employee getIssuedToEmployee() {
        return issuedToEmployee;
    }

    public void setIssuedToEmployee(Employee issuedToEmployee) {
        this.issuedToEmployee = issuedToEmployee;
    }

    public User getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(User issuedBy) {
        this.issuedBy = issuedBy;
    }

    public Integer getIssuedQuantity() {
        return issuedQuantity;
    }

    public void setIssuedQuantity(Integer issuedQuantity) {
        this.issuedQuantity = issuedQuantity;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getIssueReferenceNumber() {
        return issueReferenceNumber;
    }

    public void setIssueReferenceNumber(String issueReferenceNumber) {
        this.issueReferenceNumber = issueReferenceNumber;
    }

    public IssueStatus getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(IssueStatus issueStatus) {
        this.issueStatus = issueStatus;
    }

    
}