package com.example.demo.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;

@Table("sys_role_menu")
public class SysRoleMenu {
    @Column("role_id")
    private Long roleId;

    @Column("menu_id")
    private Long menuId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }
}
