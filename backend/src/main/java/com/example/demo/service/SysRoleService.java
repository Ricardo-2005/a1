package com.example.demo.service;

import com.example.demo.dto.SysRoleCreateDTO;
import com.example.demo.dto.SysRoleUpdateDTO;
import com.example.demo.entity.SysRole;

import java.util.List;

public interface SysRoleService {
    List<SysRole> list(String keyword);

    SysRole create(SysRoleCreateDTO dto);

    SysRole update(Long id, SysRoleUpdateDTO dto);

    void delete(Long id);
}
