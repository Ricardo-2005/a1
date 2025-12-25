package com.example.demo.service.impl;

import com.example.demo.entity.SysRoleMenu;
import com.example.demo.mapper.SysRoleMenuMapper;
import com.example.demo.service.SysRoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {
    private final SysRoleMenuMapper sysRoleMenuMapper;

    public SysRoleMenuServiceImpl(SysRoleMenuMapper sysRoleMenuMapper) {
        this.sysRoleMenuMapper = sysRoleMenuMapper;
    }

    @Override
    public List<Long> getMenuIds(Long roleId) {
        return sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        sysRoleMenuMapper.deleteByRoleId(roleId);
        for (Long menuId : menuIds) {
            SysRoleMenu relation = new SysRoleMenu();
            relation.setRoleId(roleId);
            relation.setMenuId(menuId);
            sysRoleMenuMapper.insert(relation);
        }
    }
}
