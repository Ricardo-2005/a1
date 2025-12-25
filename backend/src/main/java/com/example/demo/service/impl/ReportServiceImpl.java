package com.example.demo.service.impl;

import com.example.demo.mapper.EmployeeLeaveMapper;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.mapper.PositionTransferMapper;
import com.example.demo.service.ReportService;
import com.example.demo.vo.HireReportVO;
import com.example.demo.vo.LeaveReportVO;
import com.example.demo.vo.TransferReportVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final EmployeeMapper employeeMapper;
    private final EmployeeLeaveMapper employeeLeaveMapper;
    private final PositionTransferMapper positionTransferMapper;

    public ReportServiceImpl(EmployeeMapper employeeMapper,
                             EmployeeLeaveMapper employeeLeaveMapper,
                             PositionTransferMapper positionTransferMapper) {
        this.employeeMapper = employeeMapper;
        this.employeeLeaveMapper = employeeLeaveMapper;
        this.positionTransferMapper = positionTransferMapper;
    }

    @Override
    public List<HireReportVO> hireReport(LocalDate startDate, LocalDate endDate) {
        return employeeMapper.selectHireReport(startDate, endDate);
    }

    @Override
    public List<LeaveReportVO> leaveReport(LocalDate startDate, LocalDate endDate) {
        return employeeLeaveMapper.selectLeaveReport(startDate, endDate);
    }

    @Override
    public List<TransferReportVO> transferReport(LocalDate startDate, LocalDate endDate) {
        return positionTransferMapper.selectTransferReport(startDate, endDate);
    }
}
