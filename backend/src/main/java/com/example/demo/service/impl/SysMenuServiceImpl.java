package com.example.demo.service.impl;

import com.example.demo.dto.SysMenuCreateDTO;
import com.example.demo.dto.SysMenuUpdateDTO;
import com.example.demo.entity.SysMenu;
import com.example.demo.mapper.SysMenuMapper;
import com.example.demo.service.SysMenuService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysMenuServiceImpl implements SysMenuService {
    private final SysMenuMapper sysMenuMapper;

    public SysMenuServiceImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public List<SysMenu> list(String keyword) {
        QueryWrapper query = QueryWrapper.create().from("sys_menu");
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword + "%";
            query.where("menu_name like ? or perms like ? or path like ?", like, like, like);
        }
        query.orderBy("order_num asc, id asc");
        return sysMenuMapper.selectListByQuery(query);
    }

    @Override
    public SysMenu create(SysMenuCreateDTO dto) {
        SysMenu menu = new SysMenu();
        menu.setMenuName(dto.getMenuName());
        menu.setMenuType(dto.getMenuType());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setPerms(dto.getPerms());
        menu.setParentId(dto.getParentId());
        menu.setOrderNum(dto.getOrderNum());
        menu.setStatus(dto.getStatus());
        sysMenuMapper.insert(menu);
        return menu;
    }

    @Override
    public SysMenu update(Long id, SysMenuUpdateDTO dto) {
        SysMenu menu = sysMenuMapper.selectOneById(id);
        if (menu == null) {
            throw new IllegalArgumentException("Menu not found");
        }
        menu.setMenuName(dto.getMenuName());
        menu.setMenuType(dto.getMenuType());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setPerms(dto.getPerms());
        menu.setParentId(dto.getParentId());
        menu.setOrderNum(dto.getOrderNum());
        menu.setStatus(dto.getStatus());
        sysMenuMapper.update(menu);
        return menu;
    }

    @Override
    public void delete(Long id) {
        long children = sysMenuMapper.countByParentId(id);
        if (children > 0) {
            throw new IllegalArgumentException("Please delete child menus first");
        }
        sysMenuMapper.deleteById(id);
    }
}
