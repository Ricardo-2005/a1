# 功能规格说明书：人事管理系统（HumanResourcesManagementSystem）

版本：1.1  
状态：Draft  
创建时间：2025-12-22  
最后更新：2025-12-22  
负责人：HRM Product Team  

## 1. 概述

人事管理系统提供部门、岗位、员工、岗位调动、离职、报表、账号与权限管理能力。系统采用前后端分离架构，支持分页查询、统一响应格式与 RBAC 权限控制，满足课程设计与实训项目的交付要求。

## 2. 关键说明（Clarifications）

- 数据库初始化阶段即包含基础数据（部门、岗位、员工、账号、角色、菜单）
- 删除员工为物理删除，且同步删除账号
- 离职操作：写入离职快照 + 删除员工 + 删除账号
- 支持“恢复在职”：从离职快照重建员工与账号
- 离职后员工不可再进行岗位调动

## 3. 功能需求

### FR1 认证与 RBAC（P0）
- 账号登录 `POST /api/auth/login`
- 登录成功后返回用户信息 + 菜单列表 + 权限标识集合
- 基于 Session 的权限校验（后端拦截器）
- 前端菜单与按钮基于权限动态渲染

### FR2 部门管理（P0）
- 查询、创建、编辑、删除、分页

### FR3 岗位管理（P0）
- 查询、创建、编辑、删除、分页

### FR4 员工管理（P0）
- 查询、创建、编辑、删除（物理删除）
- 创建员工时同步创建账号
- 修改岗位自动记录调动

### FR5 岗位调动（P0）
- 记录调动前/后岗位
- 自动更新员工当前岗位
- 离职员工禁止调动

### FR6 离职管理（P1）
- 登记离职
- 写入离职快照并删除员工与账号
- 支持恢复在职

### FR7 报表管理（P1）
- 新聘、离职、调动报表（按时间范围）

### FR8 账号管理（P1）
- 分页查询
- 重置密码
- 管理员账号禁止删除

### FR9 角色/菜单/权限（P1）
- 角色管理（增删改查）
- 菜单管理（增删改查）
- 权限分配（角色-菜单）

## 4. 非功能需求

- 统一响应 `Result<T>`
- 接口分页标准化
- 前端校验与加载状态完整
- Session 权限拦截

## 5. 关键代码说明（节选）

### 5.1 登录与权限下发
路径：`backend/src/main/java/com/example/demo/controller/AuthController.java`

```java
@PostMapping("/login")
public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
    SysUser user = authService.authenticate(request.getUsername(), request.getPassword());
    if (user == null) {
        throw new IllegalArgumentException("用户名或密码错误");
    }
    SysRole role = sysRoleMapper.selectByRoleCode(user.getRoleCode());
    List<SysMenu> menus = sysMenuMapper.selectMenusByRoleId(role.getId());
    List<String> permissions = sysMenuMapper.selectPermsByRoleId(role.getId());
    session.setAttribute(SessionKeys.PERMISSIONS, permissions);
    return Result.ok(new LoginResponse(new LoginUserInfo(...), menus, permissions));
}
```

### 5.2 权限拦截
路径：`backend/src/main/java/com/example/demo/common/AuthInterceptor.java`

```java
if (session == null) {
    writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
    return false;
}
String requiredPerm = resolvePermission(path, method);
if (requiredPerm != null && !permSet.contains(requiredPerm)) {
    writeError(response, HttpServletResponse.SC_FORBIDDEN, "没有权限访问");
    return false;
}
```

### 5.3 离职（写快照 + 删除员工 + 删除账号）
路径：`backend/src/main/java/com/example/demo/service/impl/EmployeeLeaveServiceImpl.java`

```java
EmployeeLeave leave = new EmployeeLeave();
leave.setEmployeeNo(employee.getEmployeeNo());
leave.setEmployeeName(employee.getName());
leave.setDepartmentId(employee.getDepartmentId());
leave.setPositionId(employee.getPositionId());
leave.setRoleCode(sysUser != null ? sysUser.getRoleCode() : "ROLE_EMPLOYEE");
employeeLeaveMapper.insert(leave);

if (sysUser != null) {
    sysUserMapper.deleteByEmployeeId(employee.getId());
}
employeeMapper.deleteById(employee.getId());
```

### 5.4 恢复在职
路径：`backend/src/main/java/com/example/demo/service/impl/EmployeeLeaveServiceImpl.java`

```java
Employee employee = new Employee();
employee.setEmployeeNo(leave.getEmployeeNo());
employee.setName(leave.getEmployeeName());
employee.setDepartmentId(leave.getDepartmentId());
employee.setPositionId(leave.getPositionId());
employeeMapper.insert(employee);

SysRole role = sysRoleMapper.selectByRoleCode(leave.getRoleCode());
SysUser user = new SysUser();
user.setUsername(employee.getEmployeeNo());
user.setRoleId(role.getId());
user.setRoleCode(role.getRoleCode());
sysUserMapper.insert(user);
```

## 6. API 汇总

认证：
- `POST /api/auth/login`
- `GET /api/auth/me`

部门：
- `GET /api/departments`
- `GET /api/departments/all`
- `POST /api/departments`
- `PUT /api/departments/{id}`
- `DELETE /api/departments/{id}`

岗位：
- `GET /api/positions`
- `GET /api/positions/all`
- `POST /api/positions`
- `PUT /api/positions/{id}`
- `DELETE /api/positions/{id}`

员工：
- `GET /api/employees`
- `POST /api/employees`
- `PUT /api/employees/{id}`
- `DELETE /api/employees/{id}`

岗位调动：
- `GET /api/transfers`
- `POST /api/transfers`

离职：
- `GET /api/leaves`
- `POST /api/leaves`
- `PUT /api/leaves/{id}/restore`

报表：
- `GET /api/reports/hire`
- `GET /api/reports/leave`
- `GET /api/reports/transfer`

账号：
- `GET /api/users`
- `PUT /api/users/{id}/reset-password`

角色与权限：
- `GET /api/roles`
- `POST /api/roles`
- `PUT /api/roles/{id}`
- `DELETE /api/roles/{id}`
- `GET /api/menus`
- `POST /api/menus`
- `PUT /api/menus/{id}`
- `DELETE /api/menus/{id}`
- `GET /api/role-menus/{roleId}`
- `PUT /api/role-menus/{roleId}`

## 7. 数据模型说明

核心表：
- `department`
- `job_position`
- `employee`
- `position_transfer`
- `employee_leave`（保留离职快照）
- `sys_user`
- `sys_role`
- `sys_menu`
- `sys_role_menu`

初始化数据与建表 SQL：`backend/sql/hrm.sql`

## 8. 用户流程简述

- 管理员登录 → 菜单动态渲染 → 进入模块操作
- 员工管理新增员工 → 自动创建账号
- 修改岗位 → 自动记录调动
- 离职 → 写快照 + 删除员工 + 删除账号
- 恢复在职 → 由快照重建员工与账号

## 9. 测试建议（简版）

- 接口联调：登录、增删改查、调动、离职、恢复
- 权限验证：不同角色菜单/按钮显示差异
- 数据一致性：离职后账号与员工数据一致

## 10. 版本记录

版本 | 日期 | 作者 | 变更
--- | --- | --- | ---
1.1 | 2025-12-22 | HRM Product Team | 更新 RBAC、离职快照与恢复流程
