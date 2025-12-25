package com.example.demo.service;

import com.example.demo.entity.SysUser;

public interface AuthService {
    SysUser authenticate(String username, String password);
}
