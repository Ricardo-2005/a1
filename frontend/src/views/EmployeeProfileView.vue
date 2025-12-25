<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { fetchEmployeeProfile } from '@/api/employee'
import { useAuthStore } from '@/store/auth'
import type { Employee } from '@/api/types'

const authStore = useAuthStore()
const loading = ref(false)
const profile = ref<Employee | null>(null)
const hasProfile = computed(() => Boolean(profile.value))

const loadProfile = async () => {
  loading.value = true
  try {
    const res = await fetchEmployeeProfile()
    if (res.code === 0) {
      profile.value = res.data
      if (!res.data) {
        ElMessage.warning('当前账号未绑定员工信息')
      }
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<template>
  <div class="card-panel content">
    <PageHeader title="个人信息" subtitle="查看个人基本信息" />
    <el-skeleton v-if="loading" rows="6" animated />
    <el-descriptions v-else :column="2" border>
      <el-descriptions-item label="账号">{{ authStore.user?.user.username }}</el-descriptions-item>
      <el-descriptions-item label="角色">{{ authStore.user?.user.roleName ?? authStore.user?.user.roleCode }}</el-descriptions-item>
      <el-descriptions-item label="工号">{{ hasProfile ? profile?.employeeNo : '-' }}</el-descriptions-item>
      <el-descriptions-item label="姓名">{{ hasProfile ? profile?.name : '-' }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ hasProfile ? profile?.gender : '-' }}</el-descriptions-item>
      <el-descriptions-item label="电话">{{ hasProfile ? profile?.phone : '-' }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ hasProfile ? profile?.email : '-' }}</el-descriptions-item>
      <el-descriptions-item label="部门ID">{{ hasProfile ? profile?.departmentId : '-' }}</el-descriptions-item>
      <el-descriptions-item label="岗位ID">{{ hasProfile ? profile?.positionId : '-' }}</el-descriptions-item>
      <el-descriptions-item label="入职日期">{{ hasProfile ? profile?.hireDate : '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ hasProfile ? profile?.status : '-' }}</el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<style scoped>
.content {
  padding: 2rem;
}
</style>
