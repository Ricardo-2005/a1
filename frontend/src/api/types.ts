export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  pageNumber: number
  pageSize: number
  totalRow: number
  totalPage: number
  records: T[]
}

export interface Department {
  id: number
  name: string
  code: string
  description?: string
}

export interface Position {
  id: number
  name: string
  code: string
  description?: string
}

export interface Employee {
  id: number
  employeeNo: string
  name: string
  gender?: string
  phone?: string
  email?: string
  departmentId: number
  positionId: number
  hireDate: string
  status: string
}

export interface EmployeeVO extends Employee {
  departmentName?: string
  positionName?: string
}

export interface PositionTransfer {
  id: number
  employeeId: number
  fromPositionId: number
  toPositionId: number
  transferDate: string
  reason?: string
}

export interface PositionTransferVO {
  id: number
  employeeId: number
  employeeName?: string
  fromPositionId: number
  fromPositionName?: string
  toPositionId: number
  toPositionName?: string
  transferDate: string
  reason?: string
}

export interface EmployeeLeaveVO {
  id: number
  employeeId: number | null
  employeeNo?: string
  employeeName?: string
  leaveDate: string
  reason?: string
}

export interface SysUserVO {
  id: number
  username: string
  roleCode: string
  roleName?: string
  roleLevel?: number
  employeeId?: number | null
  employeeNo?: string
  employeeName?: string
  status?: number
  createdAt?: string
  updatedAt?: string
}

export interface SysRole {
  id: number
  roleName: string
  roleCode: string
  roleLevel: number
  roleSort: number
  status: number
}

export interface SysMenu {
  id: number
  menuName: string
  menuType: 'M' | 'C' | 'F'
  path?: string
  component?: string
  perms?: string
  parentId?: number
  orderNum?: number
  status?: number
}
