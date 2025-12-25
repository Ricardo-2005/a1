import http from './http'
import type { ApiResponse, SysMenu } from './types'

export interface MenuQuery {
  keyword?: string
}

export const fetchMenus = (params?: MenuQuery) =>
  http.get<ApiResponse<SysMenu[]>>('/menus', { params })

export const createMenu = (data: Omit<SysMenu, 'id'>) =>
  http.post<ApiResponse<SysMenu>>('/menus', data)

export const updateMenu = (id: number, data: Omit<SysMenu, 'id'>) =>
  http.put<ApiResponse<SysMenu>>(`/menus/${id}`, data)

export const deleteMenu = (id: number) =>
  http.delete<ApiResponse<void>>(`/menus/${id}`)
