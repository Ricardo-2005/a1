package com.example.demo.service.impl;

import com.example.demo.entity.SysUser;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.service.SysUserService;
import com.example.demo.vo.SysUserVO;
import com.mybatisflex.core.paginate.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {
    private static final String ROLE_SYS_ADMIN = "ROLE_SYS_ADMIN";
    private static final String DEFAULT_PASSWORD = "123456";

    private final SysUserMapper sysUserMapper;

    public SysUserServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public Page<SysUserVO> page(String keyword, String role, int pageNumber, int pageSize) {
        long totalRow = sysUserMapper.countUsers(keyword, role);
        Page<SysUserVO> page = Page.of(pageNumber, pageSize);
        if (totalRow == 0) {
            page.setRecords(Collections.emptyList());
            page.setTotalRow(0);
            page.setTotalPage(0);
            return page;
        }
        long offset = (long) (pageNumber - 1) * pageSize;
        List<SysUserVO> records = sysUserMapper.selectUsers(keyword, role, offset, pageSize);
        page.setRecords(records);
        page.setTotalRow(totalRow);
        page.setTotalPage((totalRow + pageSize - 1) / pageSize);
        return page;
    }

    @Override
    public void resetPassword(Long id, String password) {
        SysUser user = sysUserMapper.selectOneById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        String nextPassword = (password == null || password.isBlank()) ? DEFAULT_PASSWORD : password;
        user.setPassword(nextPassword);
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.update(user);
    }

    @Override
    public void delete(Long id) {
        SysUser user = sysUserMapper.selectOneById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (ROLE_SYS_ADMIN.equals(user.getRoleCode())) {
            throw new IllegalArgumentException("Admin account cannot be deleted");
        }
        sysUserMapper.deleteById(id);
    }
}
