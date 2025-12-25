package com.example.demo.mapper;

import com.example.demo.entity.SysRole;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode} LIMIT 1")
    SysRole selectByRoleCode(@Param("roleCode") String roleCode);
}
