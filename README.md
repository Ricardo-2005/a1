# 人事管理系统（HumanResourcesManagementSystem）

## 项目简介

本项目是基于 Spring Boot + MyBatis-Flex + MySQL + Vue3 的人事管理系统。系统覆盖部门、岗位、员工、岗位调动、离职、报表、账号与权限（RBAC）等核心业务，适用于课程设计与实训项目提交。

## 技术栈

- 后端：Spring Boot 3.x、MyBatis-Flex、MySQL 8.x
- 前端：Vue 3 + Vite、Element Plus、Axios、Pinia、Vue Router
- 权限：RBAC（角色/菜单/权限标识）

## 目录结构

```
HumanResourcesManagementSystem/
├─ backend/               # Spring Boot 后端
├─ frontend/              # Vue3 前端
├─ backend/sql/hrm.sql    # 建表与初始化数据
├─ CLASS_DIAGRAM_ALL.puml
├─ CLASS_DIAGRAM_ALL_DETAILED.puml
├─ CLASS_FLOW_DOCUMENTATION.md
└─ FUNCTIONAL_DOCUMENTATION.md
```

## 快速启动

### 1) 数据库

在 MySQL 中执行：`backend/sql/hrm.sql`  
注意：如果已有旧库，请先执行 `ALTER TABLE employee_leave ...`（见文末“数据库变更说明”）。

### 2) 后端

在 `backend` 目录：

```
./mvnw spring-boot:run
```

### 3) 前端

在 `frontend` 目录：

```
npm install
npm run dev
```

## 默认账号

- 管理员：`admin / admin123`
- 员工：`E001 / 123456`（示例）

## 核心规则

- 员工必须绑定部门与岗位
- 岗位调动会自动记录调动日志
- 离职时：写入离职快照 + 删除员工 + 删除账号
- 恢复在职：从离职快照重建员工与账号

## 权限管理入口

管理员登录后进入：`/admin/permissions`  
支持角色管理、菜单管理、权限分配。

## 常见问题

- **页面提示 403**：说明账号无权限，请检查角色权限分配。
- **离职列表无数据**：先在员工管理执行“离职”，或执行初始化 SQL 中的离职示例。
- **字体加载报错**：已移除外链字体，前端可正常使用系统字体。

## 数据库变更说明（离职快照）

如果你是在旧库上升级，请执行以下 SQL：

```sql
ALTER TABLE employee_leave
  MODIFY COLUMN employee_id BIGINT NULL,
  ADD COLUMN employee_no VARCHAR(50),
  ADD COLUMN employee_name VARCHAR(100),
  ADD COLUMN department_id BIGINT,
  ADD COLUMN position_id BIGINT,
  ADD COLUMN gender VARCHAR(20),
  ADD COLUMN phone VARCHAR(50),
  ADD COLUMN email VARCHAR(120),
  ADD COLUMN hire_date DATE,
  ADD COLUMN role_code VARCHAR(100);

ALTER TABLE employee_leave
  DROP FOREIGN KEY fk_leave_employee,
  ADD CONSTRAINT fk_leave_employee FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE SET NULL;
```

## 文档

- `FUNCTIONAL_DOCUMENTATION.md`：功能规格说明
- `CLASS_FLOW_DOCUMENTATION.md`：类关系与业务流程
- `CLASS_DIAGRAM_ALL.puml`：整体类图
- `CLASS_DIAGRAM_ALL_DETAILED.puml`：详细类图
