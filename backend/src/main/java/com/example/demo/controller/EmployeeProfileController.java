package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.SessionKeys;
import com.example.demo.entity.Employee;
import com.example.demo.mapper.EmployeeMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee")
public class EmployeeProfileController {
    private final EmployeeMapper employeeMapper;

    public EmployeeProfileController(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @GetMapping("/profile")
    public Result<Employee> profile(HttpSession session) {
        Object employeeId = session.getAttribute(SessionKeys.EMPLOYEE_ID);
        if (employeeId == null) {
            return Result.ok(null);
        }
        Employee employee = employeeMapper.selectOneById(Long.valueOf(String.valueOf(employeeId)));
        if (employee == null) {
            throw new IllegalArgumentException("员工信息不存在");
        }
        return Result.ok(employee);
    }
}
