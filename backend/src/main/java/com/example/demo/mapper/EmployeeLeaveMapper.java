package com.example.demo.mapper;

import com.example.demo.entity.EmployeeLeave;
import com.example.demo.vo.EmployeeLeaveVO;
import com.example.demo.vo.LeaveReportVO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EmployeeLeaveMapper extends BaseMapper<EmployeeLeave> {

    @Select("""
        <script>
        SELECT l.id,
               l.employee_id AS employeeId,
               COALESCE(e.employee_no, l.employee_no) AS employeeNo,
               COALESCE(e.name, l.employee_name) AS employeeName,
               l.leave_date AS leaveDate,
               l.reason AS reason
        FROM employee_leave l
        LEFT JOIN employee e ON l.employee_id = e.id
        <where>
            <if test='employeeId != null'>
                AND l.employee_id = #{employeeId}
            </if>
            <if test='startDate != null'>
                AND l.leave_date &gt;= #{startDate}
            </if>
            <if test='endDate != null'>
                AND l.leave_date &lt;= #{endDate}
            </if>
        </where>
        ORDER BY l.leave_date DESC
        LIMIT #{offset}, #{size}
        </script>
        """)
    List<EmployeeLeaveVO> selectLeaves(@Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("offset") long offset,
            @Param("size") long size);

    @Select("""
        <script>
        SELECT COUNT(*)
        FROM employee_leave l
        <where>
            <if test='employeeId != null'>
                AND l.employee_id = #{employeeId}
            </if>
            <if test='startDate != null'>
                AND l.leave_date &gt;= #{startDate}
            </if>
            <if test='endDate != null'>
                AND l.leave_date &lt;= #{endDate}
            </if>
        </where>
        </script>
        """)
    long countLeaves(@Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Select("""
        <script>
        SELECT COALESCE(e.employee_no, l.employee_no) AS employeeNo,
               COALESCE(e.name, l.employee_name) AS name,
               d.name AS departmentName,
               p.name AS positionName,
               l.leave_date AS leaveDate,
               l.reason AS reason
        FROM employee_leave l
        LEFT JOIN employee e ON l.employee_id = e.id
        LEFT JOIN department d ON d.id = COALESCE(e.department_id, l.department_id)
        LEFT JOIN job_position p ON p.id = COALESCE(e.position_id, l.position_id)
        <where>
            <if test='startDate != null'>
                AND l.leave_date &gt;= #{startDate}
            </if>
            <if test='endDate != null'>
                AND l.leave_date &lt;= #{endDate}
            </if>
        </where>
        ORDER BY l.leave_date DESC
        </script>
        """)
    List<LeaveReportVO> selectLeaveReport(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
