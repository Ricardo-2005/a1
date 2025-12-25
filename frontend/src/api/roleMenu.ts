import http from './http'
import type { ApiResponse } from './types'

export const fetchRoleMenus = (roleId: number) =>
  http.get<ApiResponse<number[]>>(`/role-menus/${roleId}`)

export const saveRoleMenus = (roleId: number, menuIds: number[]) =>
  http.put<ApiResponse<void>>(`/role-menus/${roleId}`, { menuIds })
