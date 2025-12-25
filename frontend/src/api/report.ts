import http from './http'
import type { ApiResponse } from './types'

export interface ReportQuery {
  startDate?: string
  endDate?: string
}

export interface HireReportRow {
  employeeNo: string
  name: string
  departmentName: string
  positionName: string
  hireDate: string
}

export interface LeaveReportRow {
  employeeNo: string
  name: string
  departmentName: string
  positionName: string
  leaveDate: string
  reason?: string
}

export interface TransferReportRow {
  employeeNo: string
  name: string
  fromPositionName: string
  toPositionName: string
  transferDate: string
  reason?: string
}

export const fetchHireReport = (params: ReportQuery) =>
  http.get<ApiResponse<HireReportRow[]>>('/reports/hire', { params })

export const fetchLeaveReport = (params: ReportQuery) =>
  http.get<ApiResponse<LeaveReportRow[]>>('/reports/leave', { params })

export const fetchTransferReport = (params: ReportQuery) =>
  http.get<ApiResponse<TransferReportRow[]>>('/reports/transfer', { params })
