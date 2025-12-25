package com.example.demo.service.impl;

import com.example.demo.entity.SysUser;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.service.AuthService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final SysUserMapper sysUserMapper;

    public AuthServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public SysUser authenticate(String username, String password) {
        SysUser user = sysUserMapper.selectOneByQuery(
                QueryWrapper.create().from("sys_user").where("username = ?", username));
        if (user == null) {
            return null;
        }
        if (!password.equals(user.getPassword())) {
            return null;
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return null;
        }
        return user;
    }
}
