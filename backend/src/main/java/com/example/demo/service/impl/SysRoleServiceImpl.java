package com.example.demo.service.impl;

import com.example.demo.dto.SysRoleCreateDTO;
import com.example.demo.dto.SysRoleUpdateDTO;
import com.example.demo.entity.SysRole;
import com.example.demo.mapper.SysRoleMapper;
import com.example.demo.service.SysRoleService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {
    private final SysRoleMapper sysRoleMapper;

    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public List<SysRole> list(String keyword) {
        QueryWrapper query = QueryWrapper.create().from("sys_role");
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword + "%";
            query.where("role_name like ? or role_code like ?", like, like);
        }
        query.orderBy("role_sort asc, id asc");
        return sysRoleMapper.selectListByQuery(query);
    }

    @Override
    public SysRole create(SysRoleCreateDTO dto) {
        SysRole role = new SysRole();
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        role.setRoleLevel(dto.getRoleLevel());
        role.setRoleSort(dto.getRoleSort());
        role.setStatus(dto.getStatus());
        sysRoleMapper.insert(role);
        return role;
    }

    @Override
    public SysRole update(Long id, SysRoleUpdateDTO dto) {
        SysRole role = sysRoleMapper.selectOneById(id);
        if (role == null) {
            throw new IllegalArgumentException("Role not found");
        }
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        role.setRoleLevel(dto.getRoleLevel());
        role.setRoleSort(dto.getRoleSort());
        role.setStatus(dto.getStatus());
        sysRoleMapper.update(role);
        return role;
    }

    @Override
    public void delete(Long id) {
        sysRoleMapper.deleteById(id);
    }
}
