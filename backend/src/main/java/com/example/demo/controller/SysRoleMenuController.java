package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.RoleMenuSaveDTO;
import com.example.demo.service.SysRoleMenuService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role-menus")
public class SysRoleMenuController {
    private final SysRoleMenuService sysRoleMenuService;

    public SysRoleMenuController(SysRoleMenuService sysRoleMenuService) {
        this.sysRoleMenuService = sysRoleMenuService;
    }

    @GetMapping("/{roleId}")
    public Result<List<Long>> list(@PathVariable Long roleId) {
        return Result.ok(sysRoleMenuService.getMenuIds(roleId));
    }

    @PutMapping("/{roleId}")
    public Result<Void> save(@PathVariable Long roleId, @Valid @RequestBody RoleMenuSaveDTO dto) {
        sysRoleMenuService.saveRoleMenus(roleId, dto.getMenuIds());
        return Result.ok();
    }
}
