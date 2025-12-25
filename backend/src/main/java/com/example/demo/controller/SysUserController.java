package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.ResetPasswordDTO;
import com.example.demo.service.SysUserService;
import com.example.demo.vo.SysUserVO;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class SysUserController {
    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @GetMapping
    public Result<Page<SysUserVO>> page(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String role,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return Result.ok(sysUserService.page(keyword, role, page, size));
    }

    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody(required = false) ResetPasswordDTO dto) {
        String password = dto == null ? null : dto.getPassword();
        sysUserService.resetPassword(id, password);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return Result.ok();
    }
}
