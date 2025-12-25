import http from './http'
import type { ApiResponse, EmployeeLeaveVO, PageResult } from './types'

export interface LeaveCreatePayload {
  employeeId: number
  leaveDate: string
  reason?: string
}

export interface LeaveQuery {
  employeeId?: number
  startDate?: string
  endDate?: string
  page?: number
  size?: number
}

export const createLeave = (data: LeaveCreatePayload) =>
  http.post<ApiResponse<void>>('/leaves', data)

export const fetchLeaves = (params: LeaveQuery) =>
  http.get<ApiResponse<PageResult<EmployeeLeaveVO>>>('/leaves', { params })

export const restoreLeave = (id: number) =>
  http.put<ApiResponse<void>>(`/leaves/${id}/restore`)
