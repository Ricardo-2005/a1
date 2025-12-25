package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.EmployeeLeaveCreateDTO;
import com.example.demo.entity.EmployeeLeave;
import com.example.demo.service.EmployeeLeaveService;
import com.example.demo.vo.EmployeeLeaveVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/leaves")
public class EmployeeLeaveController {
    private final EmployeeLeaveService employeeLeaveService;

    public EmployeeLeaveController(EmployeeLeaveService employeeLeaveService) {
        this.employeeLeaveService = employeeLeaveService;
    }

    @PostMapping
    public Result<EmployeeLeave> create(@Valid @RequestBody EmployeeLeaveCreateDTO dto) {
        return Result.ok(employeeLeaveService.create(dto));
    }

    @GetMapping
    public Result<Page<EmployeeLeaveVO>> page(@RequestParam(required = false) Long employeeId,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return Result.ok(employeeLeaveService.page(employeeId, startDate, endDate, page, size));
    }

    @PutMapping("/{id}/restore")
    public Result<Void> restore(@PathVariable Long id) {
        employeeLeaveService.restore(id);
        return Result.ok();
    }
}
