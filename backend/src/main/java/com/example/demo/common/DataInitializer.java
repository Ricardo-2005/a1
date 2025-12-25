package com.example.demo.common;

import com.example.demo.common.enums.EmployeeStatus;
import com.example.demo.entity.Department;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Position;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysUser;
import com.example.demo.mapper.DepartmentMapper;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.mapper.PositionMapper;
import com.example.demo.mapper.SysRoleMapper;
import com.example.demo.mapper.SysUserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final String ROLE_SYS_ADMIN = "ROLE_SYS_ADMIN";
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private static final String TABLE_EMPLOYEE = "employee";

    private final DepartmentMapper departmentMapper;
    private final PositionMapper positionMapper;
    private final EmployeeMapper employeeMapper;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;

    public DataInitializer(DepartmentMapper departmentMapper,
                           PositionMapper positionMapper,
                           EmployeeMapper employeeMapper,
                           SysUserMapper sysUserMapper,
                           SysRoleMapper sysRoleMapper) {
        this.departmentMapper = departmentMapper;
        this.positionMapper = positionMapper;
        this.employeeMapper = employeeMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public void run(String... args) {
        Department hr = ensureDepartment("D001", "人力资源部", "负责招聘与员工关系");
        Department tech = ensureDepartment("D002", "技术部", "负责产品研发");

        Position hrPosition = ensurePosition("P001", "HR专员", "招聘与员工档案管理");
        Position backendPosition = ensurePosition("P002", "后端工程师", "后端系统开发与维护");

        Employee adminEmployee = ensureEmployee(new EmployeeSeed(
                "A000",
                "系统管理员",
                "男",
                "13800000000",
                "admin@example.com",
                hr.getId(),
                hrPosition.getId(),
                LocalDate.of(2024, 1, 1)
        ));

        Employee e1 = ensureEmployee(new EmployeeSeed(
                "E001",
                "张伟",
                "男",
                "13800000001",
                "zhangwei@example.com",
                hr.getId(),
                hrPosition.getId(),
                LocalDate.of(2024, 1, 15)
        ));

        Employee e2 = ensureEmployee(new EmployeeSeed(
                "E002",
                "李娜",
                "女",
                "13800000002",
                "lina@example.com",
                tech.getId(),
                backendPosition.getId(),
                LocalDate.of(2024, 3, 20)
        ));

        Long adminEmployeeId = adminEmployee == null ? null : adminEmployee.getId();
        SysUser adminUser = ensureUser("admin", "admin123", ROLE_SYS_ADMIN, adminEmployeeId);
        enforceAdminRole(adminUser, adminEmployee);
        ensureUser("zhangwei", "123456", ROLE_EMPLOYEE, e1.getId());
        ensureUser("lina", "123456", ROLE_EMPLOYEE, e2.getId());
    }

    private Department ensureDepartment(String code, String name, String description) {
        Department existing = departmentMapper.selectOneByQuery(
                QueryWrapper.create().from("department").where("code = ?", code));
        if (existing != null) {
            return existing;
        }
        Department department = new Department();
        department.setCode(code);
        department.setName(name);
        department.setDescription(description);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        departmentMapper.insert(department);
        return department;
    }

    private Position ensurePosition(String code, String name, String description) {
        Position existing = positionMapper.selectOneByQuery(
                QueryWrapper.create().from("job_position").where("code = ?", code));
        if (existing != null) {
            return existing;
        }
        Position position = new Position();
        position.setCode(code);
        position.setName(name);
        position.setDescription(description);
        position.setCreatedAt(LocalDateTime.now());
        position.setUpdatedAt(LocalDateTime.now());
        positionMapper.insert(position);
        return position;
    }

    private Employee ensureEmployee(EmployeeSeed seed) {
        Employee existing = employeeMapper.selectOneByQuery(
                QueryWrapper.create().from(TABLE_EMPLOYEE).where("employee_no = ?", seed.employeeNo()));
        if (existing != null) {
            return existing;
        }
        Employee employee = new Employee();
        employee.setEmployeeNo(seed.employeeNo());
        employee.setName(seed.name());
        employee.setGender(seed.gender());
        employee.setPhone(seed.phone());
        employee.setEmail(seed.email());
        employee.setDepartmentId(seed.departmentId());
        employee.setPositionId(seed.positionId());
        employee.setHireDate(seed.hireDate());
        employee.setStatus(EmployeeStatus.ACTIVE.name());
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        employeeMapper.insert(employee);
        return employee;
    }

    private SysUser ensureUser(String username, String password, String roleCode, Long employeeId) {
        SysUser existing = sysUserMapper.selectOneByQuery(
                QueryWrapper.create().from("sys_user").where("username = ?", username));
        if (existing != null) {
            return existing;
        }
        SysRole role = sysRoleMapper.selectByRoleCode(roleCode);
        if (role == null) {
            return null;
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoleId(role.getId());
        user.setRoleCode(role.getRoleCode());
        user.setEmployeeId(employeeId);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.insert(user);
        return user;
    }

    private void enforceAdminRole(SysUser adminUser, Employee adminEmployee) {
        if (adminUser == null) {
            return;
        }
        boolean roleMismatch = !ROLE_SYS_ADMIN.equals(adminUser.getRoleCode());
        boolean employeeMismatch = adminEmployee != null && !adminEmployee.getId().equals(adminUser.getEmployeeId());
        if (roleMismatch || employeeMismatch) {
            SysRole role = sysRoleMapper.selectByRoleCode(ROLE_SYS_ADMIN);
            if (role == null) {
                return;
            }
            adminUser.setRoleId(role.getId());
            adminUser.setRoleCode(role.getRoleCode());
            if (adminEmployee != null) {
                adminUser.setEmployeeId(adminEmployee.getId());
            }
            adminUser.setUpdatedAt(LocalDateTime.now());
            sysUserMapper.update(adminUser);
        }
    }

    private record EmployeeSeed(
            String employeeNo,
            String name,
            String gender,
            String phone,
            String email,
            Long departmentId,
            Long positionId,
            LocalDate hireDate
    ) {
    }
}
