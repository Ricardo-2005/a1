package com.example.demo.service;

import com.example.demo.vo.SysUserVO;
import com.mybatisflex.core.paginate.Page;

public interface SysUserService {
    Page<SysUserVO> page(String keyword, String role, int pageNumber, int pageSize);

    void resetPassword(Long id, String password);

    void delete(Long id);
}
