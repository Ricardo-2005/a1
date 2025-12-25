package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class EmployeeLeaveCreateDTO {
    @NotNull
    private Long employeeId;

    @NotNull
    private LocalDate leaveDate;

    private String reason;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(LocalDate leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
