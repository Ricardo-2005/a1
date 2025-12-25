package com.example.demo.vo;

import java.time.LocalDate;

public class TransferReportVO {
    private String employeeNo;
    private String name;
    private String fromPositionName;
    private String toPositionName;
    private LocalDate transferDate;
    private String reason;

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFromPositionName() {
        return fromPositionName;
    }

    public void setFromPositionName(String fromPositionName) {
        this.fromPositionName = fromPositionName;
    }

    public String getToPositionName() {
        return toPositionName;
    }

    public void setToPositionName(String toPositionName) {
        this.toPositionName = toPositionName;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
