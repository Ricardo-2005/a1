package com.example.demo.service;

import java.util.List;

public interface SysRoleMenuService {
    List<Long> getMenuIds(Long roleId);

    void saveRoleMenus(Long roleId, List<Long> menuIds);
}
