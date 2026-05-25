package com.example.entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.entity.base.BaseEntity;
import com.example.enums.RequestStatus;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_request")
public class InventoryRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "request_number",
            nullable = false,
            unique = true)
    private String requestNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "remarks")
    private String remarks;

    @OneToMany(
            mappedBy = "request",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
        )
        private List<RequestItems> requestItems = new ArrayList<>();
        
    @OneToMany(
        mappedBy = "request",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<RequestApproval> approvals =
            new ArrayList<>();


    public InventoryRequest(Long requestId, Employee employee, String requestNumber, RequestStatus requestStatus,
            LocalDateTime requestDate, String remarks, List<RequestItems> requestItems,
            List<RequestApproval> approvals) {
        this.requestId = requestId;
        this.employee = employee;
        this.requestNumber = requestNumber;
        this.requestStatus = requestStatus;
        this.requestDate = requestDate;
        this.remarks = remarks;
        this.requestItems = requestItems;
        this.approvals = approvals;
    }

    public InventoryRequest() {
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<RequestItems> getRequestItems() {
        return requestItems;
    }

    public void setRequestItems(List<RequestItems> requestItems) {
        this.requestItems = requestItems;
    }

    public List<RequestApproval> getApprovals() {
        return approvals;
    }

    public void setApprovals(List<RequestApproval> approvals) {
        this.approvals = approvals;
    }

    
}