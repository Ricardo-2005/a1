package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.SessionKeys;
import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.SysMenu;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysUser;
import com.example.demo.mapper.SysMenuMapper;
import com.example.demo.mapper.SysRoleMapper;
import com.example.demo.service.AuthService;
import com.example.demo.vo.LoginResponse;
import com.example.demo.vo.LoginUserInfo;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/auth", "/auth"})
public class AuthController {
    private final AuthService authService;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;

    public AuthController(AuthService authService,
                          SysRoleMapper sysRoleMapper,
                          SysMenuMapper sysMenuMapper) {
        this.authService = authService;
        this.sysRoleMapper = sysRoleMapper;
        this.sysMenuMapper = sysMenuMapper;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        SysUser user = authService.authenticate(request.getUsername(), request.getPassword());
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        SysRole role = sysRoleMapper.selectByRoleCode(user.getRoleCode());
        if (role == null) {
            throw new IllegalArgumentException("角色信息缺失");
        }
        List<SysMenu> menus = sysMenuMapper.selectMenusByRoleId(role.getId());
        List<String> permissions = sysMenuMapper.selectPermsByRoleId(role.getId());

        session.setAttribute(SessionKeys.USER_ID, user.getId());
        session.setAttribute(SessionKeys.USERNAME, user.getUsername());
        session.setAttribute(SessionKeys.ROLE_ID, role.getId());
        session.setAttribute(SessionKeys.ROLE_CODE, role.getRoleCode());
        session.setAttribute(SessionKeys.ROLE_LEVEL, role.getRoleLevel());
        session.setAttribute(SessionKeys.EMPLOYEE_ID, user.getEmployeeId());
        session.setAttribute(SessionKeys.PERMISSIONS, permissions);

        LoginUserInfo userInfo = new LoginUserInfo(
                user.getId(),
                user.getUsername(),
                role.getRoleCode(),
                role.getRoleLevel(),
                role.getRoleName(),
                user.getEmployeeId()
        );
        LoginResponse response = new LoginResponse(userInfo, menus, permissions);
        return Result.ok(response);
    }

    @GetMapping("/me")
    public Result<LoginResponse> me(HttpSession session) {
        Object userId = session.getAttribute(SessionKeys.USER_ID);
        Object username = session.getAttribute(SessionKeys.USERNAME);
        Object roleId = session.getAttribute(SessionKeys.ROLE_ID);
        Object roleCode = session.getAttribute(SessionKeys.ROLE_CODE);
        Object roleLevel = session.getAttribute(SessionKeys.ROLE_LEVEL);
        Object employeeId = session.getAttribute(SessionKeys.EMPLOYEE_ID);
        if (userId == null || roleCode == null || username == null || roleId == null || roleLevel == null) {
            return Result.fail("未登录");
        }
        SysRole role = sysRoleMapper.selectByRoleCode(String.valueOf(roleCode));
        if (role == null) {
            return Result.fail("角色信息缺失");
        }
        List<SysMenu> menus = sysMenuMapper.selectMenusByRoleId(role.getId());
        List<String> permissions = sysMenuMapper.selectPermsByRoleId(role.getId());
        LoginUserInfo userInfo = new LoginUserInfo(
                Long.valueOf(String.valueOf(userId)),
                String.valueOf(username),
                String.valueOf(roleCode),
                Integer.valueOf(String.valueOf(roleLevel)),
                role.getRoleName(),
                employeeId == null ? null : Long.valueOf(String.valueOf(employeeId))
        );
        LoginResponse response = new LoginResponse(userInfo, menus, permissions);
        return Result.ok(response);
    }
}
