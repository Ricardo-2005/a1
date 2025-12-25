package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PositionTransferCreateDTO {
    @NotNull
    private Long employeeId;

    @NotNull
    private Long toPositionId;

    @NotNull
    private LocalDate transferDate;

    private String reason;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getToPositionId() {
        return toPositionId;
    }

    public void setToPositionId(Long toPositionId) {
        this.toPositionId = toPositionId;
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
