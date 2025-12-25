package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.SysMenuCreateDTO;
import com.example.demo.dto.SysMenuUpdateDTO;
import com.example.demo.entity.SysMenu;
import com.example.demo.service.SysMenuService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class SysMenuController {
    private final SysMenuService sysMenuService;

    public SysMenuController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    @GetMapping
    public Result<List<SysMenu>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(sysMenuService.list(keyword));
    }

    @PostMapping
    public Result<SysMenu> create(@Valid @RequestBody SysMenuCreateDTO dto) {
        return Result.ok(sysMenuService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<SysMenu> update(@PathVariable Long id, @Valid @RequestBody SysMenuUpdateDTO dto) {
        return Result.ok(sysMenuService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysMenuService.delete(id);
        return Result.ok();
    }
}
