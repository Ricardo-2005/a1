package com.example.demo.service.impl;

import com.example.demo.common.enums.EmployeeStatus;
import com.example.demo.dto.EmployeeLeaveCreateDTO;
import com.example.demo.entity.Employee;
import com.example.demo.entity.EmployeeLeave;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysUser;
import com.example.demo.mapper.EmployeeLeaveMapper;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.mapper.DepartmentMapper;
import com.example.demo.mapper.PositionMapper;
import com.example.demo.mapper.SysRoleMapper;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.service.EmployeeLeaveService;
import com.example.demo.vo.EmployeeLeaveVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private static final String DEFAULT_PASSWORD = "123456";
    private final EmployeeLeaveMapper employeeLeaveMapper;
    private final EmployeeMapper employeeMapper;
    private final DepartmentMapper departmentMapper;
    private final PositionMapper positionMapper;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;

    public EmployeeLeaveServiceImpl(EmployeeLeaveMapper employeeLeaveMapper,
                                    EmployeeMapper employeeMapper,
                                    DepartmentMapper departmentMapper,
                                    PositionMapper positionMapper,
                                    SysUserMapper sysUserMapper,
                                    SysRoleMapper sysRoleMapper) {
        this.employeeLeaveMapper = employeeLeaveMapper;
        this.employeeMapper = employeeMapper;
        this.departmentMapper = departmentMapper;
        this.positionMapper = positionMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    @Transactional
    public EmployeeLeave create(EmployeeLeaveCreateDTO dto) {
        Employee employee = employeeMapper.selectOneById(dto.getEmployeeId());
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found");
        }
        SysUser sysUser = sysUserMapper.selectOneByQuery(
                QueryWrapper.create()
                        .from("sys_user")
                        .where("employee_id = ?", employee.getId()));
        EmployeeLeave leave = new EmployeeLeave();
        leave.setEmployeeId(dto.getEmployeeId());
        leave.setEmployeeNo(employee.getEmployeeNo());
        leave.setEmployeeName(employee.getName());
        leave.setDepartmentId(employee.getDepartmentId());
        leave.setPositionId(employee.getPositionId());
        leave.setGender(employee.getGender());
        leave.setPhone(employee.getPhone());
        leave.setEmail(employee.getEmail());
        leave.setHireDate(employee.getHireDate());
        if (sysUser != null) {
            leave.setRoleCode(sysUser.getRoleCode());
        } else {
            leave.setRoleCode(ROLE_EMPLOYEE);
        }
        leave.setLeaveDate(dto.getLeaveDate());
        leave.setReason(dto.getReason());
        leave.setCreatedAt(LocalDateTime.now());
        employeeLeaveMapper.insert(leave);

        if (sysUser != null) {
            sysUserMapper.deleteByEmployeeId(employee.getId());
        }
        employeeMapper.deleteById(employee.getId());
        return leave;
    }

    @Override
    public Page<EmployeeLeaveVO> page(Long employeeId, LocalDate startDate, LocalDate endDate,
                                      int pageNumber, int pageSize) {
        long totalRow = employeeLeaveMapper.countLeaves(employeeId, startDate, endDate);
        Page<EmployeeLeaveVO> page = Page.of(pageNumber, pageSize);
        if (totalRow == 0) {
            page.setRecords(Collections.emptyList());
            page.setTotalRow(0);
            page.setTotalPage(0);
            return page;
        }
        long offset = (long) (pageNumber - 1) * pageSize;
        page.setRecords(employeeLeaveMapper.selectLeaves(employeeId, startDate, endDate, offset, pageSize));
        page.setTotalRow(totalRow);
        page.setTotalPage((totalRow + pageSize - 1) / pageSize);
        return page;
    }

    @Override
    @Transactional
    public void restore(Long leaveId) {
        EmployeeLeave leave = employeeLeaveMapper.selectOneById(leaveId);
        if (leave == null) {
            throw new IllegalArgumentException("Leave record not found");
        }
        Employee existing = employeeMapper.selectOneByQuery(
                QueryWrapper.create()
                        .from("employee")
                        .where("employee_no = ?", leave.getEmployeeNo()));
        if (existing != null) {
            throw new IllegalArgumentException("Employee already exists");
        }
        if (leave.getEmployeeNo() == null || leave.getEmployeeName() == null) {
            throw new IllegalArgumentException("Leave record missing snapshot");
        }
        if (leave.getDepartmentId() == null || leave.getPositionId() == null) {
            throw new IllegalArgumentException("Leave record missing position data");
        }
        if (departmentMapper.selectOneById(leave.getDepartmentId()) == null) {
            throw new IllegalArgumentException("Department not found");
        }
        if (positionMapper.selectOneById(leave.getPositionId()) == null) {
            throw new IllegalArgumentException("Position not found");
        }
        Employee employee = new Employee();
        employee.setEmployeeNo(leave.getEmployeeNo());
        employee.setName(leave.getEmployeeName());
        employee.setGender(leave.getGender());
        employee.setPhone(leave.getPhone());
        employee.setEmail(leave.getEmail());
        employee.setDepartmentId(leave.getDepartmentId());
        employee.setPositionId(leave.getPositionId());
        employee.setHireDate(leave.getHireDate() != null ? leave.getHireDate() : LocalDate.now());
        employee.setStatus(EmployeeStatus.ACTIVE.name());
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        employeeMapper.insert(employee);

        String roleCode = leave.getRoleCode();
        SysRole role = null;
        if (roleCode != null && !roleCode.isBlank()) {
            role = sysRoleMapper.selectByRoleCode(roleCode);
        }
        if (role == null) {
            role = sysRoleMapper.selectByRoleCode(ROLE_EMPLOYEE);
        }
        if (role == null) {
            throw new IllegalArgumentException("Employee role not found");
        }
        SysUser user = new SysUser();
        user.setUsername(employee.getEmployeeNo());
        user.setPassword(DEFAULT_PASSWORD);
        user.setRoleId(role.getId());
        user.setRoleCode(role.getRoleCode());
        user.setEmployeeId(employee.getId());
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.insert(user);

        employeeLeaveMapper.deleteById(leaveId);
    }
}
