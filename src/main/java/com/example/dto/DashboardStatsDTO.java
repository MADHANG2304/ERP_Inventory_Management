package com.example.dto;

public class DashboardStatsDTO {

    private Long totalRequests;
    private Long draftRequests;
    private Long pendingRequests;
    private Long approvedRequests;
    private Long issuedRequests;
    private Long pendingApprovals;
    private Long lowStockItems;
    private Long outOfStockItems;
    private Long totalEmployees;
    private Long totalItems;
   
    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(Long totalRequests, Long draftRequests, Long pendingRequests, Long approvedRequests,
            Long issuedRequests, Long pendingApprovals, Long lowStockItems, Long outOfStockItems, Long totalEmployees,
            Long totalItems) {
        this.totalRequests = totalRequests;
        this.draftRequests = draftRequests;
        this.pendingRequests = pendingRequests;
        this.approvedRequests = approvedRequests;
        this.issuedRequests = issuedRequests;
        this.pendingApprovals = pendingApprovals;
        this.lowStockItems = lowStockItems;
        this.outOfStockItems = outOfStockItems;
        this.totalEmployees = totalEmployees;
        this.totalItems = totalItems;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Long getDraftRequests() {
        return draftRequests;
    }

    public void setDraftRequests(Long draftRequests) {
        this.draftRequests = draftRequests;
    }

    public Long getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(Long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public Long getApprovedRequests() {
        return approvedRequests;
    }

    public void setApprovedRequests(Long approvedRequests) {
        this.approvedRequests = approvedRequests;
    }

    public Long getIssuedRequests() {
        return issuedRequests;
    }

    public void setIssuedRequests(Long issuedRequests) {
        this.issuedRequests = issuedRequests;
    }

    public Long getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(Long pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public Long getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(Long lowStockItems) {
        this.lowStockItems = lowStockItems;
    }

    public Long getOutOfStockItems() {
        return outOfStockItems;
    }

    public void setOutOfStockItems(Long outOfStockItems) {
        this.outOfStockItems = outOfStockItems;
    }

    public Long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(Long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }

    
}