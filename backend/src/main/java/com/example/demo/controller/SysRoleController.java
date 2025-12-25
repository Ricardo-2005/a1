package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.SysRoleCreateDTO;
import com.example.demo.dto.SysRoleUpdateDTO;
import com.example.demo.entity.SysRole;
import com.example.demo.service.SysRoleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class SysRoleController {
    private final SysRoleService sysRoleService;

    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @GetMapping
    public Result<List<SysRole>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(sysRoleService.list(keyword));
    }

    @PostMapping
    public Result<SysRole> create(@Valid @RequestBody SysRoleCreateDTO dto) {
        return Result.ok(sysRoleService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<SysRole> update(@PathVariable Long id, @Valid @RequestBody SysRoleUpdateDTO dto) {
        return Result.ok(sysRoleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.delete(id);
        return Result.ok();
    }
}
