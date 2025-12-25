package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.ReportService;
import com.example.demo.vo.HireReportVO;
import com.example.demo.vo.LeaveReportVO;
import com.example.demo.vo.TransferReportVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/hire")
    public Result<List<HireReportVO>> hireReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.ok(reportService.hireReport(startDate, endDate));
    }

    @GetMapping("/leave")
    public Result<List<LeaveReportVO>> leaveReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.ok(reportService.leaveReport(startDate, endDate));
    }

    @GetMapping("/transfer")
    public Result<List<TransferReportVO>> transferReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.ok(reportService.transferReport(startDate, endDate));
    }
}
