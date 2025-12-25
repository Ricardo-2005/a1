import http from './http'
import type { ApiResponse, PageResult, Position } from './types'

export interface PositionQuery {
  keyword?: string
  page?: number
  size?: number
}

export const fetchPositions = (params: PositionQuery) =>
  http.get<ApiResponse<PageResult<Position>>>('/positions', { params })

export const fetchAllPositions = () =>
  http.get<ApiResponse<Position[]>>('/positions/all')

export const createPosition = (data: Omit<Position, 'id'>) =>
  http.post<ApiResponse<Position>>('/positions', data)

export const updatePosition = (id: number, data: Omit<Position, 'id'>) =>
  http.put<ApiResponse<Position>>(`/positions/${id}`, data)

export const deletePosition = (id: number) =>
  http.delete<ApiResponse<void>>(`/positions/${id}`)
