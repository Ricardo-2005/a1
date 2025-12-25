# 类关系与流程文档（HumanResourcesManagementSystem）

本文档描述系统主要类之间的关系与核心业务流程，适用于课程设计说明与技术汇报。

## 1. 分层结构（整体）

```
Controller  ->  Service  ->  Mapper  ->  Entity
                     |
                    DTO / VO
```

说明：
- Controller：处理 HTTP 请求，统一返回 `Result<T>`
- Service：业务逻辑与事务控制
- Mapper：MyBatis-Flex 数据访问层
- Entity：数据库表实体
- DTO：请求入参
- VO：响应视图对象

## 2. 主要模块关系

### 2.1 认证与权限

```
AuthController
  -> AuthService
      -> SysUserMapper -> SysUser
  -> SysRoleMapper / SysMenuMapper -> SysRole / SysMenu
AuthInterceptor
  -> SessionKeys (读取权限集合)
WebConfig
  -> 注册 AuthInterceptor
```

### 2.2 RBAC 管理（角色/菜单/权限）

```
SysRoleController -> SysRoleService -> SysRoleMapper -> SysRole
SysMenuController -> SysMenuService -> SysMenuMapper -> SysMenu
SysRoleMenuController -> SysRoleMenuService -> SysRoleMenuMapper -> SysRoleMenu
```

### 2.3 部门 / 岗位

```
DepartmentController -> DepartmentService -> DepartmentMapper -> Department
PositionController -> PositionService -> PositionMapper -> Position
```

### 2.4 员工管理

```
EmployeeController
  -> EmployeeService
      -> EmployeeMapper -> Employee
      -> DepartmentMapper / PositionMapper
      -> SysUserMapper / SysRoleMapper
      -> PositionTransferMapper (岗位变更自动记录)
```

### 2.5 岗位调动

```
PositionTransferController
  -> PositionTransferService
      -> PositionTransferMapper -> PositionTransfer
      -> EmployeeMapper / PositionMapper
  -> PositionTransferVO
```

### 2.6 员工离职

```
EmployeeLeaveController
  -> EmployeeLeaveService
      -> EmployeeLeaveMapper -> EmployeeLeave（离职快照）
      -> EmployeeMapper / SysUserMapper / SysRoleMapper
```

### 2.7 报表

```
ReportController
  -> ReportService
      -> EmployeeMapper / EmployeeLeaveMapper / PositionTransferMapper
  -> HireReportVO / LeaveReportVO / TransferReportVO
```

### 2.8 账号管理

```
SysUserController
  -> SysUserService
      -> SysUserMapper -> SysUser
  -> SysUserVO
```

### 2.9 初始化数据

```
DataInitializer
  -> DepartmentMapper / PositionMapper / EmployeeMapper / SysUserMapper / SysRoleMapper
  -> 预置部门、岗位、员工、账号、角色
```

## 3. 关键业务流程

### 3.1 登录 + 权限下发

1. 前端调用 `POST /api/auth/login`
2. `AuthService.authenticate` 校验账号
3. 根据角色加载菜单与权限集合
4. 写入 Session（userId / roleCode / roleLevel / permissions）
5. 前端保存用户信息并动态渲染菜单

### 3.2 权限拦截

1. 请求进入 `AuthInterceptor`
2. 无 Session 返回 401
3. 根据 URL + Method 解析权限标识
4. Session 内无权限返回 403

### 3.3 新增员工

1. `POST /api/employees`
2. 校验部门/岗位
3. 插入 `employee`
4. 创建账号 `sys_user`，默认角色 `ROLE_EMPLOYEE`

### 3.4 修改岗位（记录调动）

1. 员工管理修改岗位
2. `EmployeeService.update` 更新员工
3. 岗位发生变化时自动写入 `position_transfer`

### 3.5 离职（物理删除）

1. `POST /api/leaves`
2. 写入离职快照（employee_leave，含员工信息）
3. 删除 `sys_user`
4. 删除 `employee`

### 3.6 恢复在职

1. `PUT /api/leaves/{id}/restore`
2. 从离职快照重建 employee + sys_user
3. 删除离职记录

### 3.7 岗位调动

1. `POST /api/transfers`
2. 校验员工状态、岗位有效性
3. 写入调动记录并更新员工岗位

## 4. 前端流程概览

### 4.1 登录与路由守卫

1. 登录成功写入 Pinia `authStore`
2. 根据 `roleLevel` 跳转 `/admin` 或 `/employee`
3. `router.beforeEach` 检查 `perm`，无权限跳转 403

### 4.2 菜单与按钮权限

1. 侧边栏使用后端 `menus` 动态渲染
2. 按钮权限使用 `hasPerm('xxx')` 控制显示

## 5. 关键约束

- 员工必须关联部门与岗位
- 离职后不可再次调动
- 删除员工同步删除账号
- 权限完全由数据库配置（RBAC）
