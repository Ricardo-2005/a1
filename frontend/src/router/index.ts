import { createRouter, createWebHistory } from 'vue-router'

import LoginView from '@/views/LoginView.vue'
import MainLayout from '@/layouts/MainLayout.vue'
import DepartmentView from '@/views/DepartmentView.vue'
import EmployeeView from '@/views/EmployeeView.vue'
import PositionView from '@/views/PositionView.vue'
import TransferView from '@/views/TransferView.vue'
import ReportsView from '@/views/ReportsView.vue'
import LeaveView from '@/views/LeaveView.vue'
import EmployeeProfileView from '@/views/EmployeeProfileView.vue'
import AccountView from '@/views/AccountView.vue'
import ForbiddenView from '@/views/ForbiddenView.vue'
import PermissionView from '@/views/PermissionView.vue'
import { useAuthStore } from '@/store/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/admin',
      component: MainLayout,
      meta: { role: 'admin' },
      redirect: '/admin/departments',
      children: [
        {
          path: 'departments',
          name: 'admin-departments',
          component: DepartmentView,
          meta: { perm: 'system:department:list' },
        },
        {
          path: 'positions',
          name: 'admin-positions',
          component: PositionView,
          meta: { perm: 'system:position:list' },
        },
        {
          path: 'employees',
          name: 'admin-employees',
          component: EmployeeView,
          meta: { perm: 'system:employee:list' },
        },
        {
          path: 'transfers',
          name: 'admin-transfers',
          component: TransferView,
          meta: { perm: 'system:transfer:list' },
        },
        {
          path: 'leaves',
          name: 'admin-leaves',
          component: LeaveView,
          meta: { perm: 'system:leave:list' },
        },
        {
          path: 'reports',
          name: 'admin-reports',
          component: ReportsView,
          meta: { perm: 'system:report:view' },
        },
        {
          path: 'users',
          name: 'admin-users',
          component: AccountView,
          meta: { perm: 'system:user:list' },
        },
        {
          path: 'permissions',
          name: 'admin-permissions',
          component: PermissionView,
          meta: { perm: 'system:perm:manage' },
        },
      ],
    },
    {
      path: '/employee',
      component: MainLayout,
      meta: { role: 'employee' },
      redirect: '/employee/profile',
      children: [
        {
          path: 'profile',
          name: 'employee-profile',
          component: EmployeeProfileView,
          meta: { perm: 'system:profile:view' },
        },
      ],
    },
    {
      path: '/403',
      name: 'forbidden',
      component: ForbiddenView,
    },
    {
      path: '/',
      redirect: '/login',
    },
  ],
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (to.path === '/login') {
    return true
  }
  if (!authStore.isLoggedIn) {
    return { path: '/login' }
  }
  const requiredPerm = to.matched.find((record) => record.meta.perm)?.meta.perm as string | undefined
  if (requiredPerm && !authStore.hasPerm(requiredPerm)) {
    return { path: '/403' }
  }
  return true
})

export default router
