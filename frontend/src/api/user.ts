import http from './http'
import type { ApiResponse, PageResult, SysUserVO } from './types'

export interface UserQuery {
  keyword?: string
  role?: string
  page?: number
  size?: number
}

export interface ResetPasswordPayload {
  password?: string
}

export const fetchUsers = (params: UserQuery) =>
  http.get<ApiResponse<PageResult<SysUserVO>>>('/users', { params })

export const resetUserPassword = (id: number, data: ResetPasswordPayload) =>
  http.put<ApiResponse<void>>(`/users/${id}/reset-password`, data)

export const deleteUser = (id: number) =>
  http.delete<ApiResponse<void>>(`/users/${id}`)
