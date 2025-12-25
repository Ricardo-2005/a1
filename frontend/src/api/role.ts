import http from './http'
import type { ApiResponse, SysRole } from './types'

export interface RoleQuery {
  keyword?: string
}

export const fetchRoles = (params?: RoleQuery) =>
  http.get<ApiResponse<SysRole[]>>('/roles', { params })

export const createRole = (data: Omit<SysRole, 'id'>) =>
  http.post<ApiResponse<SysRole>>('/roles', data)

export const updateRole = (id: number, data: Omit<SysRole, 'id'>) =>
  http.put<ApiResponse<SysRole>>(`/roles/${id}`, data)

export const deleteRole = (id: number) =>
  http.delete<ApiResponse<void>>(`/roles/${id}`)
