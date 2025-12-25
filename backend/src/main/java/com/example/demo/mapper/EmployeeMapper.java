package com.example.demo.mapper;

import com.example.demo.entity.Employee;
import com.example.demo.vo.EmployeeVO;
import com.example.demo.vo.HireReportVO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    @Select("""
        <script>
        SELECT e.id,
               e.employee_no AS employeeNo,
               e.name,
               e.gender,
               e.phone,
               e.email,
               e.department_id AS departmentId,
               e.position_id AS positionId,
               e.hire_date AS hireDate,
               e.status,
               d.name AS departmentName,
               p.name AS positionName
        FROM employee e
        LEFT JOIN department d ON e.department_id = d.id
        LEFT JOIN job_position p ON e.position_id = p.id
        <where>
            <if test='keyword != null and keyword != ""'>
                AND (e.name LIKE CONCAT('%', #{keyword}, '%') OR e.employee_no LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test='departmentId != null'>
                AND e.department_id = #{departmentId}
            </if>
            <if test='positionId != null'>
                AND e.position_id = #{positionId}
            </if>
            <if test='status != null and status != ""'>
                AND e.status = #{status}
            </if>
        </where>
        ORDER BY e.id DESC
        LIMIT #{offset}, #{size}
        </script>
        """)
    List<EmployeeVO> selectEmployees(@Param("keyword") String keyword,
            @Param("departmentId") Long departmentId,
            @Param("positionId") Long positionId,
            @Param("status") String status,
            @Param("offset") long offset,
            @Param("size") long size);

    @Select("""
        <script>
        SELECT COUNT(*)
        FROM employee e
        <where>
            <if test='keyword != null and keyword != ""'>
                AND (e.name LIKE CONCAT('%', #{keyword}, '%') OR e.employee_no LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test='departmentId != null'>
                AND e.department_id = #{departmentId}
            </if>
            <if test='positionId != null'>
                AND e.position_id = #{positionId}
            </if>
            <if test='status != null and status != ""'>
                AND e.status = #{status}
            </if>
        </where>
        </script>
        """)
    long countEmployees(@Param("keyword") String keyword,
            @Param("departmentId") Long departmentId,
            @Param("positionId") Long positionId,
            @Param("status") String status);

    @Select("""
        <script>
        SELECT e.employee_no AS employeeNo,
               e.name AS name,
               d.name AS departmentName,
               p.name AS positionName,
               e.hire_date AS hireDate
        FROM employee e
        LEFT JOIN department d ON e.department_id = d.id
        LEFT JOIN job_position p ON e.position_id = p.id
        <where>
            <if test='startDate != null'>
                AND e.hire_date &gt;= #{startDate}
            </if>
            <if test='endDate != null'>
                AND e.hire_date &lt;= #{endDate}
            </if>
        </where>
        ORDER BY e.hire_date DESC
        </script>
        """)
    List<HireReportVO> selectHireReport(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
