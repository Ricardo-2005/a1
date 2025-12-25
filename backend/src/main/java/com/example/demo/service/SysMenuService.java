package com.example.demo.service;

import com.example.demo.dto.SysMenuCreateDTO;
import com.example.demo.dto.SysMenuUpdateDTO;
import com.example.demo.entity.SysMenu;

import java.util.List;

public interface SysMenuService {
    List<SysMenu> list(String keyword);

    SysMenu create(SysMenuCreateDTO dto);

    SysMenu update(Long id, SysMenuUpdateDTO dto);

    void delete(Long id);
}
