package com.example.demo.vo;

import com.example.demo.entity.SysMenu;

import java.util.List;

public class LoginResponse {
    private LoginUserInfo user;
    private List<SysMenu> menus;
    private List<String> permissions;

    public LoginResponse() {
    }

    public LoginResponse(LoginUserInfo user, List<SysMenu> menus, List<String> permissions) {
        this.user = user;
        this.menus = menus;
        this.permissions = permissions;
    }

    public LoginUserInfo getUser() {
        return user;
    }

    public void setUser(LoginUserInfo user) {
        this.user = user;
    }

    public List<SysMenu> getMenus() {
        return menus;
    }

    public void setMenus(List<SysMenu> menus) {
        this.menus = menus;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
