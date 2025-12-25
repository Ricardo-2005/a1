import { defineStore } from 'pinia'
import type { LoginResponse } from '@/api/auth'

const STORAGE_KEY = 'hrm-user'

const readStoredUser = (): LoginResponse | null => {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw) as LoginResponse
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: readStoredUser(),
  }),
  getters: {
    roleCode: (state) => state.user?.user.roleCode ?? '',
    roleLevel: (state) => state.user?.user.roleLevel ?? 0,
    isLoggedIn: (state) => Boolean(state.user),
    menus: (state) => state.user?.menus ?? [],
    permissions: (state) => state.user?.permissions ?? [],
    hasPerm: (state) => {
      return (perm: string) => {
        return state.user?.permissions?.includes(perm) ?? false
      }
    },
  },
  actions: {
    setUser(user: LoginResponse) {
      this.user = user
      localStorage.setItem(STORAGE_KEY, JSON.stringify(user))
    },
    clearUser() {
      this.user = null
      localStorage.removeItem(STORAGE_KEY)
    },
  },
})
