package com.example.demo.service;

import com.example.demo.dto.EmployeeLeaveCreateDTO;
import com.example.demo.entity.EmployeeLeave;
import com.example.demo.vo.EmployeeLeaveVO;
import com.mybatisflex.core.paginate.Page;

import java.time.LocalDate;

public interface EmployeeLeaveService {
    EmployeeLeave create(EmployeeLeaveCreateDTO dto);

    Page<EmployeeLeaveVO> page(Long employeeId, LocalDate startDate, LocalDate endDate,
                               int pageNumber, int pageSize);

    void restore(Long leaveId);
}
