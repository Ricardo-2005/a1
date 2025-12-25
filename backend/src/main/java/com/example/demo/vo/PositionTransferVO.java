package com.example.demo.vo;

import java.time.LocalDate;

public class PositionTransferVO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long fromPositionId;
    private String fromPositionName;
    private Long toPositionId;
    private String toPositionName;
    private LocalDate transferDate;
    private String reason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Long getFromPositionId() {
        return fromPositionId;
    }

    public void setFromPositionId(Long fromPositionId) {
        this.fromPositionId = fromPositionId;
    }

    public String getFromPositionName() {
        return fromPositionName;
    }

    public void setFromPositionName(String fromPositionName) {
        this.fromPositionName = fromPositionName;
    }

    public Long getToPositionId() {
        return toPositionId;
    }

    public void setToPositionId(Long toPositionId) {
        this.toPositionId = toPositionId;
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
