import http from './http'
import type { ApiResponse, Department, PageResult } from './types'

export interface DepartmentQuery {
  keyword?: string
  page?: number
  size?: number
}

export const fetchDepartments = (params: DepartmentQuery) =>
  http.get<ApiResponse<PageResult<Department>>>('/departments', { params })

export const fetchAllDepartments = () =>
  http.get<ApiResponse<Department[]>>('/departments/all')

export const createDepartment = (data: Omit<Department, 'id'>) =>
  http.post<ApiResponse<Department>>('/departments', data)

export const updateDepartment = (id: number, data: Omit<Department, 'id'>) =>
  http.put<ApiResponse<Department>>(`/departments/${id}`, data)

export const deleteDepartment = (id: number) =>
  http.delete<ApiResponse<void>>(`/departments/${id}`)
