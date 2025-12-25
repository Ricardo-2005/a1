import http from './http'
import type { ApiResponse, PageResult, PositionTransfer, PositionTransferVO } from './types'

export interface TransferQuery {
  employeeId?: number
  startDate?: string
  endDate?: string
  page?: number
  size?: number
}

export interface TransferCreatePayload {
  employeeId: number
  toPositionId: number
  transferDate: string
  reason?: string
}

export const fetchTransfers = (params: TransferQuery) =>
  http.get<ApiResponse<PageResult<PositionTransferVO>>>('/transfers', { params })

export const createTransfer = (data: TransferCreatePayload) =>
  http.post<ApiResponse<PositionTransfer>>('/transfers', data)
