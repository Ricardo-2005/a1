import http from './http'
import type { ApiResponse, Employee, EmployeeVO, PageResult } from './types'

export interface EmployeeQuery {
  keyword?: string
  departmentId?: number
  positionId?: number
  status?: string
  page?: number
  size?: number
}

export type EmployeeCreatePayload = Omit<Employee, 'id' | 'status'>

export type EmployeeUpdatePayload = Omit<Employee, 'id' | 'employeeNo' | 'hireDate' | 'status'>

export const fetchEmployees = (params: EmployeeQuery) =>
  http.get<ApiResponse<PageResult<EmployeeVO>>>('/employees', { params })

export const createEmployee = (data: EmployeeCreatePayload) =>
  http.post<ApiResponse<Employee>>('/employees', data)

export const updateEmployee = (id: number, data: EmployeeUpdatePayload) =>
  http.put<ApiResponse<Employee>>(`/employees/${id}`, data)

export const deleteEmployee = (id: number) =>
  http.delete<ApiResponse<void>>(`/employees/${id}`)

export const fetchEmployeeProfile = () =>
  http.get<ApiResponse<Employee>>('/employee/profile')
