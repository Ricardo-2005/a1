package com.example.demo.mapper;

import com.example.demo.entity.SysUser;
import com.example.demo.vo.SysUserVO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Delete("DELETE FROM sys_user WHERE employee_id = #{employeeId}")
    int deleteByEmployeeId(@Param("employeeId") Long employeeId);

    @Select("""
        <script>
        SELECT u.id,
               u.username,
               u.role_code AS roleCode,
               r.role_name AS roleName,
               r.role_level AS roleLevel,
               u.employee_id AS employeeId,
               e.employee_no AS employeeNo,
               e.name AS employeeName,
               u.status,
               u.created_at AS createdAt,
               u.updated_at AS updatedAt
        FROM sys_user u
        LEFT JOIN sys_role r ON u.role_id = r.id
        LEFT JOIN employee e ON u.employee_id = e.id
        <where>
            <if test='keyword != null and keyword != ""'>
                AND (u.username LIKE CONCAT('%', #{keyword}, '%')
                     OR e.name LIKE CONCAT('%', #{keyword}, '%')
                     OR e.employee_no LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test='role != null and role != ""'>
                AND u.role_code = #{role}
            </if>
        </where>
        ORDER BY u.id DESC
        LIMIT #{offset}, #{size}
        </script>
        """)
    List<SysUserVO> selectUsers(@Param("keyword") String keyword,
            @Param("role") String role,
            @Param("offset") long offset,
            @Param("size") long size);

    @Select("""
        <script>
        SELECT COUNT(*)
        FROM sys_user u
        LEFT JOIN sys_role r ON u.role_id = r.id
        LEFT JOIN employee e ON u.employee_id = e.id
        <where>
            <if test='keyword != null and keyword != ""'>
                AND (u.username LIKE CONCAT('%', #{keyword}, '%')
                     OR e.name LIKE CONCAT('%', #{keyword}, '%')
                     OR e.employee_no LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test='role != null and role != ""'>
                AND u.role_code = #{role}
            </if>
        </where>
        </script>
        """)
    long countUsers(@Param("keyword") String keyword, @Param("role") String role);
}
