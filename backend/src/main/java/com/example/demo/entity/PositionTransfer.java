package com.example.demo.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("position_transfer")
public class PositionTransfer {
    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column("employee_id")
    private Long employeeId;

    @Column("from_position_id")
    private Long fromPositionId;

    @Column("to_position_id")
    private Long toPositionId;

    @Column("transfer_date")
    private LocalDate transferDate;

    private String reason;

    @Column("created_at")
    private LocalDateTime createdAt;

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

    public Long getFromPositionId() {
        return fromPositionId;
    }

    public void setFromPositionId(Long fromPositionId) {
        this.fromPositionId = fromPositionId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
