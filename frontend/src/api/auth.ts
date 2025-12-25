import http from './http'
import type { ApiResponse } from './types'

export interface MenuItem {
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

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginUserInfo {
  id: number
  username: string
  roleCode: string
  roleLevel: number
  roleName?: string
  employeeId?: number | null
}

export interface LoginResponse {
  user: LoginUserInfo
  menus: MenuItem[]
  permissions: string[]
}

export const login = (data: LoginRequest) =>
  http.post<ApiResponse<LoginResponse>>('/auth/login', data)

export const fetchMe = () => http.get<ApiResponse<LoginResponse>>('/auth/me')
