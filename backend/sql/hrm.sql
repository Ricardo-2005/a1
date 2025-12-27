CREATE DATABASE IF NOT EXISTS hrm DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hrm;

CREATE TABLE department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE job_position (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_no VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(20),
    phone VARCHAR(50),
    email VARCHAR(120),
    department_id BIGINT NOT NULL,
    position_id BIGINT NOT NULL,
    hire_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_employee_department (department_id),
    INDEX idx_employee_position (position_id),
    INDEX idx_employee_status (status),
    CONSTRAINT fk_employee_department FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE CASCADE,
    CONSTRAINT fk_employee_position FOREIGN KEY (position_id) REFERENCES job_position(id)
);

CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(100) NOT NULL,
    role_code VARCHAR(100) NOT NULL UNIQUE,
    role_level INT NOT NULL,
    role_sort INT NOT NULL,
    status TINYINT NOT NULL,
    INDEX idx_role_level (role_level)
);

CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    menu_name VARCHAR(100) NOT NULL,
    menu_type CHAR(1) NOT NULL,
    path VARCHAR(200),
    component VARCHAR(200),
    perms VARCHAR(200),
    parent_id BIGINT NOT NULL DEFAULT 0,
    order_num INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id),
    CONSTRAINT fk_role_menu_role FOREIGN KEY (role_id) REFERENCES sys_role(id),
    CONSTRAINT fk_role_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(id)
);

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role_id BIGINT NOT NULL,
    role_code VARCHAR(100) NOT NULL,
    employee_id BIGINT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_sys_user_employee (employee_id),
    INDEX idx_sys_user_role (role_id),
    CONSTRAINT fk_sys_user_role FOREIGN KEY (role_id) REFERENCES sys_role(id),
    CONSTRAINT fk_sys_user_employee FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
);

CREATE TABLE position_transfer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    from_position_id BIGINT NOT NULL,
    to_position_id BIGINT NOT NULL,
    transfer_date DATE NOT NULL,
    reason VARCHAR(255),
    created_at DATETIME NOT NULL,
    INDEX idx_transfer_employee (employee_id),
    INDEX idx_transfer_date (transfer_date),
    CONSTRAINT fk_transfer_employee FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE,
    CONSTRAINT fk_transfer_from_position FOREIGN KEY (from_position_id) REFERENCES job_position(id),
    CONSTRAINT fk_transfer_to_position FOREIGN KEY (to_position_id) REFERENCES job_position(id)
);

CREATE TABLE employee_leave (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NULL,
    employee_no VARCHAR(50),
    employee_name VARCHAR(100),
    department_id BIGINT,
    position_id BIGINT,
    gender VARCHAR(20),
    phone VARCHAR(50),
    email VARCHAR(120),
    hire_date DATE,
    role_code VARCHAR(100),
    leave_date DATE NOT NULL,
    reason VARCHAR(255),
    created_at DATETIME NOT NULL,
    INDEX idx_leave_employee (employee_id),
    INDEX idx_leave_date (leave_date),
    CONSTRAINT fk_leave_employee FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE SET NULL
);

INSERT INTO department (id, name, code, description, created_at, updated_at)
VALUES
  (1, '人力资源部', 'D001', '负责人力资源规划、招聘、员工关系与绩效管理', NOW(), NOW()),
  (2, '技术部',     'D002', '负责系统研发、技术实现与系统维护',         NOW(), NOW()),
  (3, '财务部',     'D003', '负责薪酬核算、财务管理与审计',             NOW(), NOW()),
  (4, '高管部',     'D004', '董事会与公司高层战略管理',                 NOW(), NOW())
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  description = VALUES(description),
  updated_at = NOW();


INSERT INTO job_position (id, name, code, description, created_at, updated_at)
VALUES
  (1, 'HR助理', 'P013', 'HR日常事务支持', NOW(), NOW()),
  (2, 'HR专员', 'P001', '人事基础事务处理', NOW(), NOW()),
  (3, '招聘经理', 'P009', '招聘规划与人才引进', NOW(), NOW()),
  (4, '薪酬经理', 'P010', '薪酬体系与福利管理', NOW(), NOW()),
  (5, '绩效经理', 'P011', '绩效评估与改进', NOW(), NOW()),
  (6, '员工关系经理', 'P012', '员工关系与劳动合规', NOW(), NOW()),
  (7, 'HR总监', 'P005', '人力资源统筹管理', NOW(), NOW()),
  (8, '后端工程师', 'P002', '后端系统开发与维护', NOW(), NOW()),
  (9, '普通员工', 'P015', '业务执行与团队协作', NOW(), NOW()),
  (10, '部门经理', 'P003', '技术部门管理与协调', NOW(), NOW()),
  (11, '部门总监', 'P014', '技术部门统筹与决策', NOW(), NOW()),
  (12, '财务专员', 'P004', '薪酬与财务处理', NOW(), NOW()),
  (13, '审计人员', 'P006', '审计与合规检查', NOW(), NOW()),
  (14, '董事长', 'P007', '董事会最高决策岗位', NOW(), NOW()),
  (15, '首席执行官', 'P008', '公司战略与经营管理', NOW(), NOW())
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  description = VALUES(description),
  updated_at = VALUES(updated_at);



INSERT INTO sys_role (id, role_name, role_code, role_level, role_sort, status)
VALUES
    (1, '系统超级管理员', 'ROLE_SYS_ADMIN', 1, 1, 1),
    (2, '董事长', 'ROLE_CHAIRMAN', 2, 2, 1),
    (3, '首席执行官', 'ROLE_CEO', 2, 3, 1),
    (4, '审计人员', 'ROLE_AUDITOR', 2, 4, 1),
    (5, '人力资源总监', 'ROLE_HR_DIRECTOR', 3, 5, 1),
    (6, '招聘经理', 'ROLE_RECRUIT_MANAGER', 4, 6, 1),
    (7, '薪酬经理', 'ROLE_SALARY_MANAGER', 4, 7, 1),
    (8, '绩效经理', 'ROLE_PERFORMANCE_MANAGER', 4, 8, 1),
    (9, '员工关系经理', 'ROLE_EMPLOYEE_RELATION_MANAGER', 4, 9, 1),
    (10, 'HR专员', 'ROLE_HR_SPECIALIST', 5, 10, 1),
    (11, 'HR助理', 'ROLE_HR_ASSISTANT', 5, 11, 1),
    (12, '部门总监', 'ROLE_DEPT_DIRECTOR', 6, 12, 1),
    (13, '部门经理', 'ROLE_DEPT_MANAGER', 6, 13, 1),
    (14, '普通员工', 'ROLE_EMPLOYEE', 7, 14, 1),
    (15, '财务专员', 'ROLE_FINANCE_SPECIALIST', 99, 15, 1)
ON DUPLICATE KEY UPDATE
    role_name = VALUES(role_name),
    role_level = VALUES(role_level),
    role_sort = VALUES(role_sort),
    status = VALUES(status);

INSERT INTO sys_menu (id, menu_name, menu_type, path, component, perms, parent_id, order_num, status)
VALUES
    (1, '系统管理', 'M', NULL, NULL, NULL, 0, 1, 1),
    (2, '部门管理', 'C', '/admin/departments', 'DepartmentView', 'system:department:list', 1, 1, 1),
    (3, '岗位管理', 'C', '/admin/positions', 'PositionView', 'system:position:list', 1, 2, 1),
    (4, '员工管理', 'C', '/admin/employees', 'EmployeeView', 'system:employee:list', 1, 3, 1),
    (5, '岗位调动', 'C', '/admin/transfers', 'TransferView', 'system:transfer:list', 1, 4, 1),
    (6, '员工离职', 'C', '/admin/leaves', 'LeaveView', 'system:leave:list', 1, 5, 1),
    (7, '报表管理', 'C', '/admin/reports', 'ReportsView', 'system:report:view', 1, 6, 1),
    (8, '账号管理', 'C', '/admin/users', 'AccountView', 'system:user:list', 1, 7, 1),
    (11, '权限管理', 'C', '/admin/permissions', 'PermissionView', 'system:perm:manage', 1, 8, 1),
    (9, '员工中心', 'M', NULL, NULL, NULL, 0, 2, 1),
    (10, '个人信息', 'C', '/employee/profile', 'EmployeeProfileView', 'system:profile:view', 9, 1, 1),
    (101, '新增部门', 'F', NULL, NULL, 'system:department:add', 2, 1, 1),
    (102, '编辑部门', 'F', NULL, NULL, 'system:department:edit', 2, 2, 1),
    (103, '删除部门', 'F', NULL, NULL, 'system:department:delete', 2, 3, 1),
    (104, '新增岗位', 'F', NULL, NULL, 'system:position:add', 3, 1, 1),
    (105, '编辑岗位', 'F', NULL, NULL, 'system:position:edit', 3, 2, 1),
    (106, '删除岗位', 'F', NULL, NULL, 'system:position:delete', 3, 3, 1),
    (107, '新增员工', 'F', NULL, NULL, 'system:employee:add', 4, 1, 1),
    (108, '编辑员工', 'F', NULL, NULL, 'system:employee:edit', 4, 2, 1),
    (109, '删除员工', 'F', NULL, NULL, 'system:employee:delete', 4, 3, 1),
    (110, '记录调动', 'F', NULL, NULL, 'system:transfer:add', 5, 1, 1),
    (111, '离职登记', 'F', NULL, NULL, 'system:leave:add', 6, 1, 1),
    (112, '重置密码', 'F', NULL, NULL, 'system:user:reset', 8, 1, 1),
    (113, '删除账号', 'F', NULL, NULL, 'system:user:delete', 8, 2, 1),
    (114, '角色列表', 'F', NULL, NULL, 'system:role:list', 11, 1, 1),
    (115, '新增角色', 'F', NULL, NULL, 'system:role:add', 11, 2, 1),
    (116, '编辑角色', 'F', NULL, NULL, 'system:role:edit', 11, 3, 1),
    (117, '删除角色', 'F', NULL, NULL, 'system:role:delete', 11, 4, 1),
    (118, '菜单列表', 'F', NULL, NULL, 'system:menu:list', 11, 5, 1),
    (119, '新增菜单', 'F', NULL, NULL, 'system:menu:add', 11, 6, 1),
    (120, '编辑菜单', 'F', NULL, NULL, 'system:menu:edit', 11, 7, 1),
    (121, '删除菜单', 'F', NULL, NULL, 'system:menu:delete', 11, 8, 1),
    (122, '权限分配', 'F', NULL, NULL, 'system:perm:assign', 11, 9, 1),
    (123, '离职恢复', 'F', NULL, NULL, 'system:leave:restore', 6, 2, 1)
ON DUPLICATE KEY UPDATE
    menu_name = VALUES(menu_name),
    menu_type = VALUES(menu_type),
    path = VALUES(path),
    component = VALUES(component),
    perms = VALUES(perms),
    parent_id = VALUES(parent_id),
    order_num = VALUES(order_num),
    status = VALUES(status);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.id IN (1,2,3,4,5,6,7,8,11,9,10,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123)
WHERE r.role_code IN ('ROLE_SYS_ADMIN', 'ROLE_CHAIRMAN', 'ROLE_CEO');

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.id IN (1,2,3,4,5,6,7,8,11,9,10,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123)
WHERE r.role_code = 'ROLE_AUDITOR';

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.id IN (1,2,3,4,5,6,7,8,11,9,10,101,102,103,104,105,106,107,108,110,111,112,114,118,122,123)
WHERE r.role_code IN (
    'ROLE_HR_DIRECTOR',
    'ROLE_RECRUIT_MANAGER',
    'ROLE_SALARY_MANAGER',
    'ROLE_PERFORMANCE_MANAGER',
    'ROLE_EMPLOYEE_RELATION_MANAGER',
    'ROLE_HR_SPECIALIST',
    'ROLE_HR_ASSISTANT'
);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.id IN (4,5,6,9,10,107,108,110,111,123)
WHERE r.role_code IN ('ROLE_DEPT_DIRECTOR', 'ROLE_DEPT_MANAGER');

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.id IN (7,9,10)
WHERE r.role_code IN ('ROLE_FINANCE_SPECIALIST');

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.id IN (9,10)
WHERE r.role_code IN ('ROLE_EMPLOYEE');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'A000', '系统管理员', '男', '13800000000', 'admin@example.com',
    d.id, p.id, '2024-01-01', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D001' AND p.code = 'P001'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'A000');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E001', '张伟', '男', '13800000001', 'zhangwei@example.com',
    d.id, p.id, '2024-01-15', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D001' AND p.code = 'P001'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E001');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E002', '李娜', '女', '13800000002', 'lina@example.com',
    d.id, p.id, '2024-03-20', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D002' AND p.code = 'P002'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E002');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E003', '王强', '男', '13800000003', 'wangqiang@example.com',
    d.id, p.id, '2023-11-01', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D001' AND p.code = 'P005'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E003');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E004', '赵敏', '女', '13800000004', 'zhaomin@example.com',
    d.id, p.id, '2023-12-10', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D001' AND p.code = 'P003'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E004');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E005', '刘洋', '男', '13800000005', 'liuyang@example.com',
    d.id, p.id, '2023-12-10', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D001' AND p.code = 'P003'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E005');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E006', '陈晨', '女', '13800000006', 'chenchen@example.com',
    d.id, p.id, '2023-12-10', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D001' AND p.code = 'P003'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E006');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E007', '孙悦', '女', '13800000007', 'sunyue@example.com',
    d.id, p.id, '2023-12-10', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D001' AND p.code = 'P003'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E007');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E008', '周鹏', '男', '13800000008', 'zhoupeng@example.com',
    d.id, p.id, '2023-10-01', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D002' AND p.code = 'P003'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E008');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E009', '吴杰', '男', '13800000009', 'wujie@example.com',
    d.id, p.id, '2023-10-15', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D002' AND p.code = 'P003'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E009');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E010', '何静', '女', '13800000010', 'hejing@example.com',
    d.id, p.id, '2023-09-01', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D003' AND p.code = 'P004'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E010');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E011', '乔峰', '男', '13800000011', 'qiaofeng@example.com',
    d.id, p.id, '2023-01-01', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D001' AND p.code = 'P006'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E011');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E012', '段誉', '男', '13800000012', 'duanyu@example.com',
    d.id, p.id, '2023-01-02', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D001' AND p.code = 'P006'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E012');

INSERT INTO employee (
    employee_no,
    name,
    gender,
    phone,
    email,
    department_id,
    position_id,
    hire_date,
    status,
    created_at,
    updated_at
)
SELECT
    'E013', '虚竹', '男', '13800000013', 'xuzhu@example.com',
    d.id, p.id, '2023-01-03', 'ACTIVE', NOW(), NOW()
FROM department d, job_position p
WHERE d.code = 'D001' AND p.code = 'P006'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE employee_no = 'E013');

INSERT INTO employee_leave (
  employee_id, employee_no, employee_name, department_id, position_id,
  gender, phone, email, hire_date, role_code, leave_date, reason, created_at
)
SELECT e.id, e.employee_no, e.name, e.department_id, e.position_id,
       e.gender, e.phone, e.email, e.hire_date, u.role_code,
       '2024-12-31', 'Resigned', NOW()
FROM employee e
LEFT JOIN sys_user u ON u.employee_id = e.id
WHERE e.employee_no = 'E002'
  AND NOT EXISTS (SELECT 1 FROM employee_leave WHERE employee_id = e.id);

INSERT INTO employee_leave (
  employee_id, employee_no, employee_name, department_id, position_id,
  gender, phone, email, hire_date, role_code, leave_date, reason, created_at
)
SELECT e.id, e.employee_no, e.name, e.department_id, e.position_id,
       e.gender, e.phone, e.email, e.hire_date, u.role_code,
       '2024-11-15', 'Resigned', NOW()
FROM employee e
LEFT JOIN sys_user u ON u.employee_id = e.id
WHERE e.employee_no = 'E010'
  AND NOT EXISTS (SELECT 1 FROM employee_leave WHERE employee_id = e.id);

DELETE FROM sys_user
WHERE employee_id IN (SELECT id FROM employee WHERE employee_no IN ('E002', 'E010'));

DELETE FROM employee
WHERE employee_no IN ('E002', 'E010')
  AND EXISTS (SELECT 1 FROM employee_leave l WHERE l.employee_no = employee.employee_no);

UPDATE employee e
JOIN job_position p ON p.code = 'P009'
SET e.position_id = p.id, e.updated_at = NOW()
WHERE e.employee_no = 'E004';

UPDATE employee e
JOIN job_position p ON p.code = 'P010'
SET e.position_id = p.id, e.updated_at = NOW()
WHERE e.employee_no = 'E005';

UPDATE employee e
JOIN job_position p ON p.code = 'P011'
SET e.position_id = p.id, e.updated_at = NOW()
WHERE e.employee_no = 'E006';

UPDATE employee e
JOIN job_position p ON p.code = 'P012'
SET e.position_id = p.id, e.updated_at = NOW()
WHERE e.employee_no = 'E007';

UPDATE employee e
JOIN job_position p ON p.code = 'P014'
SET e.position_id = p.id, e.updated_at = NOW()
WHERE e.employee_no = 'E008';

UPDATE employee e
JOIN department d ON d.code = 'D004'
JOIN job_position p ON p.code = 'P007'
SET e.department_id = d.id, e.position_id = p.id, e.updated_at = NOW()
WHERE e.employee_no = 'E011';

UPDATE employee e
JOIN department d ON d.code = 'D004'
JOIN job_position p ON p.code = 'P008'
SET e.department_id = d.id, e.position_id = p.id, e.updated_at = NOW()
WHERE e.employee_no = 'E012';

UPDATE employee e
JOIN department d ON d.code = 'D004'
JOIN job_position p ON p.code = 'P006'
SET e.department_id = d.id, e.position_id = p.id, e.updated_at = NOW()
WHERE e.employee_no = 'E013';

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'admin', 'admin123', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_SYS_ADMIN' AND e.employee_no = 'A000'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

UPDATE sys_user u
JOIN employee e ON e.employee_no = 'A000'
JOIN sys_role r ON r.role_code = 'ROLE_SYS_ADMIN'
SET u.employee_id = e.id,
    u.role_id = r.id,
    u.role_code = r.role_code,
    u.updated_at = NOW()
WHERE u.username = 'admin';

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E001', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_HR_SPECIALIST' AND e.employee_no = 'E001'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E001');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E002', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_EMPLOYEE' AND e.employee_no = 'E002'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E002');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E003', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_HR_DIRECTOR' AND e.employee_no = 'E003'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E003');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E004', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_RECRUIT_MANAGER' AND e.employee_no = 'E004'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E004');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E005', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_SALARY_MANAGER' AND e.employee_no = 'E005'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E005');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E006', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_PERFORMANCE_MANAGER' AND e.employee_no = 'E006'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E006');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E007', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_EMPLOYEE_RELATION_MANAGER' AND e.employee_no = 'E007'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E007');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E008', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_DEPT_DIRECTOR' AND e.employee_no = 'E008'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E008');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E009', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_DEPT_MANAGER' AND e.employee_no = 'E009'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E009');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E010', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_FINANCE_SPECIALIST' AND e.employee_no = 'E010'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E010');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E011', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_CHAIRMAN' AND e.employee_no = 'E011'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E011');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E012', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_CEO' AND e.employee_no = 'E012'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E012');

INSERT INTO sys_user (username, password, role_id, role_code, employee_id, status, created_at, updated_at)
SELECT 'E013', '123456', r.id, r.role_code, e.id, 1, NOW(), NOW()
FROM sys_role r, employee e
WHERE r.role_code = 'ROLE_AUDITOR' AND e.employee_no = 'E013'
  AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'E013');
