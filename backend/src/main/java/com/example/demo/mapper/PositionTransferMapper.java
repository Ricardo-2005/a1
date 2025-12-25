package com.example.demo.mapper;

import com.example.demo.entity.PositionTransfer;
import com.example.demo.vo.PositionTransferVO;
import com.example.demo.vo.TransferReportVO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PositionTransferMapper extends BaseMapper<PositionTransfer> {

    @Select("""
        <script>
        SELECT t.id,
               t.employee_id AS employeeId,
               e.name AS employeeName,
               t.from_position_id AS fromPositionId,
               fp.name AS fromPositionName,
               t.to_position_id AS toPositionId,
               tp.name AS toPositionName,
               t.transfer_date AS transferDate,
               t.reason AS reason
        FROM position_transfer t
        LEFT JOIN employee e ON t.employee_id = e.id
        LEFT JOIN job_position fp ON t.from_position_id = fp.id
        LEFT JOIN job_position tp ON t.to_position_id = tp.id
        <where>
            <if test='employeeId != null'>
                AND t.employee_id = #{employeeId}
            </if>
            <if test='startDate != null'>
                AND t.transfer_date &gt;= #{startDate}
            </if>
            <if test='endDate != null'>
                AND t.transfer_date &lt;= #{endDate}
            </if>
        </where>
        ORDER BY t.transfer_date DESC
        LIMIT #{offset}, #{size}
        </script>
        """)
    List<PositionTransferVO> selectTransfers(@Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("offset") long offset,
            @Param("size") long size);

    @Select("""
        <script>
        SELECT COUNT(*)
        FROM position_transfer t
        <where>
            <if test='employeeId != null'>
                AND t.employee_id = #{employeeId}
            </if>
            <if test='startDate != null'>
                AND t.transfer_date &gt;= #{startDate}
            </if>
            <if test='endDate != null'>
                AND t.transfer_date &lt;= #{endDate}
            </if>
        </where>
        </script>
        """)
    long countTransfers(@Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Select("""
        <script>
        SELECT e.employee_no AS employeeNo,
               e.name AS name,
               fp.name AS fromPositionName,
               tp.name AS toPositionName,
               t.transfer_date AS transferDate,
               t.reason AS reason
        FROM position_transfer t
        LEFT JOIN employee e ON t.employee_id = e.id
        LEFT JOIN job_position fp ON t.from_position_id = fp.id
        LEFT JOIN job_position tp ON t.to_position_id = tp.id
        <where>
            <if test='startDate != null'>
                AND t.transfer_date &gt;= #{startDate}
            </if>
            <if test='endDate != null'>
                AND t.transfer_date &lt;= #{endDate}
            </if>
        </where>
        ORDER BY t.transfer_date DESC
        </script>
        """)
    List<TransferReportVO> selectTransferReport(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
