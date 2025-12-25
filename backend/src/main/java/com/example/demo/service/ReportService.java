package com.example.demo.service;

import com.example.demo.vo.HireReportVO;
import com.example.demo.vo.LeaveReportVO;
import com.example.demo.vo.TransferReportVO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<HireReportVO> hireReport(LocalDate startDate, LocalDate endDate);

    List<LeaveReportVO> leaveReport(LocalDate startDate, LocalDate endDate);

    List<TransferReportVO> transferReport(LocalDate startDate, LocalDate endDate);
}
