package com.example.dto;

import com.example.enums.RequestStatus;

import java.time.LocalDate;

public class InventoryRequestFilterDTO {

    private String requestNumber;

    private String employeeName;

    private RequestStatus requestStatus;

    private LocalDate fromDate;

    private LocalDate toDate;

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(
            String requestNumber
    ) {
        this.requestNumber = requestNumber;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(
            String employeeName
    ) {
        this.employeeName = employeeName;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(
            RequestStatus requestStatus
    ) {
        this.requestStatus = requestStatus;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(
            LocalDate fromDate
    ) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(
            LocalDate toDate
    ) {
        this.toDate = toDate;
    }
}