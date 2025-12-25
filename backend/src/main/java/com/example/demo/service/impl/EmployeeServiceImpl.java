package com.example.demo.service.impl;

import com.example.demo.common.enums.EmployeeStatus;
import com.example.demo.dto.EmployeeCreateDTO;
import com.example.demo.dto.EmployeeUpdateDTO;
import com.example.demo.entity.Employee;
import com.example.demo.entity.PositionTransfer;
import com.example.demo.entity.SysUser;
import com.example.demo.entity.SysRole;
import com.example.demo.mapper.DepartmentMapper;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.mapper.PositionMapper;
import com.example.demo.mapper.PositionTransferMapper;
import com.example.demo.mapper.SysRoleMapper;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.service.EmployeeService;
import com.example.demo.vo.EmployeeVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String ROLE_SYS_ADMIN = "ROLE_SYS_ADMIN";

    private final EmployeeMapper employeeMapper;
    private final DepartmentMapper departmentMapper;
    private final PositionMapper positionMapper;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final PositionTransferMapper positionTransferMapper;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper,
                               DepartmentMapper departmentMapper,
                               PositionMapper positionMapper,
                               SysUserMapper sysUserMapper,
                               SysRoleMapper sysRoleMapper,
                               PositionTransferMapper positionTransferMapper) {
        this.employeeMapper = employeeMapper;
        this.departmentMapper = departmentMapper;
        this.positionMapper = positionMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.positionTransferMapper = positionTransferMapper;
    }

    @Override
    public Page<EmployeeVO> page(String keyword, Long departmentId, Long positionId, String status,
                                 int pageNumber, int pageSize) {
        long totalRow = employeeMapper.countEmployees(keyword, departmentId, positionId, status);
        Page<EmployeeVO> page = Page.of(pageNumber, pageSize);
        if (totalRow == 0) {
            page.setRecords(Collections.emptyList());
            page.setTotalRow(0);
            page.setTotalPage(0);
            return page;
        }
        long offset = (long) (pageNumber - 1) * pageSize;
        List<EmployeeVO> records = employeeMapper.selectEmployees(
                keyword, departmentId, positionId, status, offset, pageSize);
        page.setRecords(records);
        page.setTotalRow(totalRow);
        page.setTotalPage((totalRow + pageSize - 1) / pageSize);
        return page;
    }

    @Override
    @Transactional
    public Employee create(EmployeeCreateDTO dto) {
        if (departmentMapper.selectOneById(dto.getDepartmentId()) == null) {
            throw new IllegalArgumentException("Department not found");
        }
        if (positionMapper.selectOneById(dto.getPositionId()) == null) {
            throw new IllegalArgumentException("Position not found");
        }
        SysUser existingUser = sysUserMapper.selectOneByQuery(
                QueryWrapper.create().from("sys_user").where("username = ?", dto.getEmployeeNo()));
        if (existingUser != null) {
            throw new IllegalArgumentException("Account already exists");
        }
        SysRole employeeRole = sysRoleMapper.selectByRoleCode(ROLE_EMPLOYEE);
        if (employeeRole == null) {
            throw new IllegalArgumentException("Employee role not found");
        }
        Employee employee = new Employee();
        employee.setEmployeeNo(dto.getEmployeeNo());
        employee.setName(dto.getName());
        employee.setGender(dto.getGender());
        employee.setPhone(dto.getPhone());
        employee.setEmail(dto.getEmail());
        employee.setDepartmentId(dto.getDepartmentId());
        employee.setPositionId(dto.getPositionId());
        employee.setHireDate(dto.getHireDate());
        employee.setStatus(EmployeeStatus.ACTIVE.name());
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        employeeMapper.insert(employee);

        SysUser user = new SysUser();
        user.setUsername(dto.getEmployeeNo());
        user.setPassword(DEFAULT_PASSWORD);
        user.setRoleId(employeeRole.getId());
        user.setRoleCode(employeeRole.getRoleCode());
        user.setEmployeeId(employee.getId());
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.insert(user);
        return employee;
    }

    @Override
    public Employee update(Long id, EmployeeUpdateDTO dto) {
        Employee employee = employeeMapper.selectOneById(id);
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found");
        }
        if (EmployeeStatus.LEFT.name().equals(employee.getStatus())) {
            throw new IllegalArgumentException("Employee already left");
        }
        if (departmentMapper.selectOneById(dto.getDepartmentId()) == null) {
            throw new IllegalArgumentException("Department not found");
        }
        if (positionMapper.selectOneById(dto.getPositionId()) == null) {
            throw new IllegalArgumentException("Position not found");
        }
        Long fromPositionId = employee.getPositionId();
        employee.setName(dto.getName());
        employee.setGender(dto.getGender());
        employee.setPhone(dto.getPhone());
        employee.setEmail(dto.getEmail());
        employee.setDepartmentId(dto.getDepartmentId());
        employee.setPositionId(dto.getPositionId());
        employee.setUpdatedAt(LocalDateTime.now());
        employeeMapper.update(employee);

        if (fromPositionId != null && !fromPositionId.equals(dto.getPositionId())) {
            PositionTransfer transfer = new PositionTransfer();
            transfer.setEmployeeId(employee.getId());
            transfer.setFromPositionId(fromPositionId);
            transfer.setToPositionId(dto.getPositionId());
            transfer.setTransferDate(LocalDate.now());
            transfer.setReason("Updated via employee management");
            transfer.setCreatedAt(LocalDateTime.now());
            positionTransferMapper.insert(transfer);
        }
        return employee;
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeMapper.selectOneById(id);
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found");
        }
        SysUser sysUser = sysUserMapper.selectOneByQuery(
                QueryWrapper.create().from("sys_user").where("employee_id = ?", id));
        if (sysUser != null) {
            if (ROLE_SYS_ADMIN.equals(sysUser.getRoleCode())) {
                throw new IllegalArgumentException("Admin account cannot be deleted");
            }
            sysUserMapper.deleteByEmployeeId(id);
        }
        employeeMapper.deleteById(id);
    }

    @Override
    public Employee getById(Long id) {
        return employeeMapper.selectOneById(id);
    }
}
