import axios, { type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
  timeout: 10000,
})

instance.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = error?.response?.status
    if (status === 401) {
      localStorage.removeItem('hrm-user')
      ElMessage.error('登录失效，请重新登录')
      window.location.href = '/login'
    }
    if (status === 403) {
      ElMessage.error('没有权限访问该功能')
      window.location.href = '/403'
    }
    return Promise.reject(error)
  },
)

const http = {
  get<T>(url: string, config?: AxiosRequestConfig) {
    return instance.get<T, T>(url, config)
  },
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return instance.post<T, T>(url, data, config)
  },
  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return instance.put<T, T>(url, data, config)
  },
  delete<T>(url: string, config?: AxiosRequestConfig) {
    return instance.delete<T, T>(url, config)
  },
}

export default http
