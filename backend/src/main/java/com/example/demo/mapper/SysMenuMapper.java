package com.example.demo.mapper;

import com.example.demo.entity.SysMenu;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("""
        SELECT m.*
        FROM sys_menu m
        INNER JOIN sys_role_menu rm ON rm.menu_id = m.id
        WHERE rm.role_id = #{roleId}
          AND m.status = 1
        ORDER BY m.order_num ASC, m.id ASC
        """)
    List<SysMenu> selectMenusByRoleId(@Param("roleId") Long roleId);

    @Select("""
        SELECT DISTINCT m.perms
        FROM sys_menu m
        INNER JOIN sys_role_menu rm ON rm.menu_id = m.id
        WHERE rm.role_id = #{roleId}
          AND m.perms IS NOT NULL
          AND m.perms <> ''
          AND m.status = 1
        """)
    List<String> selectPermsByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT COUNT(*) FROM sys_menu WHERE parent_id = #{parentId}")
    long countByParentId(@Param("parentId") Long parentId);
}
