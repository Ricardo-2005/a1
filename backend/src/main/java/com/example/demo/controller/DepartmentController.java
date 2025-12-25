package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.DepartmentCreateDTO;
import com.example.demo.dto.DepartmentUpdateDTO;
import com.example.demo.entity.Department;
import com.example.demo.service.DepartmentService;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public Result<Page<Department>> page(@RequestParam(required = false) String keyword,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return Result.ok(departmentService.page(keyword, page, size));
    }

    @GetMapping("/all")
    public Result<List<Department>> listAll() {
        return Result.ok(departmentService.listAll());
    }

    @PostMapping
    public Result<Department> create(@Valid @RequestBody DepartmentCreateDTO dto) {
        return Result.ok(departmentService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Department> update(@PathVariable Long id,
                                     @Valid @RequestBody DepartmentUpdateDTO dto) {
        return Result.ok(departmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return Result.ok();
    }
}
