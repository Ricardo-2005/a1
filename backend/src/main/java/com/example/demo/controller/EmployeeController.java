package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.SessionKeys;
import com.example.demo.dto.EmployeeCreateDTO;
import com.example.demo.dto.EmployeeUpdateDTO;
import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;
import com.example.demo.vo.EmployeeVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public Result<Page<EmployeeVO>> page(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Long departmentId,
                                         @RequestParam(required = false) Long positionId,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpSession session) {
        Object roleLevel = session.getAttribute(SessionKeys.ROLE_LEVEL);
        Object employeeId = session.getAttribute(SessionKeys.EMPLOYEE_ID);
        if (roleLevel != null && employeeId != null) {
            int level = Integer.parseInt(String.valueOf(roleLevel));
            if (level == 6) {
                Employee self = employeeService.getById(Long.valueOf(String.valueOf(employeeId)));
                if (self != null) {
                    departmentId = self.getDepartmentId();
                }
            }
        }
        return Result.ok(employeeService.page(keyword, departmentId, positionId, status, page, size));
    }

    @PostMapping
    public Result<Employee> create(@Valid @RequestBody EmployeeCreateDTO dto) {
        return Result.ok(employeeService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Employee> update(@PathVariable Long id, @Valid @RequestBody EmployeeUpdateDTO dto) {
        return Result.ok(employeeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return Result.ok();
    }
}
