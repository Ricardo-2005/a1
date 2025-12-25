<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { fetchUsers, resetUserPassword } from '@/api/user'
import { fetchRoles } from '@/api/role'
import type { SysRole, SysUserVO } from '@/api/types'
import { useAuthStore } from '@/store/auth'

const loading = ref(false)
const items = ref<SysUserVO[]>([])
const total = ref(0)
const authStore = useAuthStore()
const roles = ref<SysRole[]>([])

const query = reactive({
  keyword: '',
  role: '',
  page: 1,
  size: 10,
})

const canReset = computed(() => authStore.hasPerm('system:user:reset'))
const roleOptions = computed(() => roles.value.filter((role) => role.status !== 0))

const loadRoles = async () => {
  const res = await fetchRoles()
  if (res.code === 0) {
    roles.value = res.data
  } else {
    ElMessage.error(res.message)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await fetchUsers(query)
    if (res.code === 0) {
      items.value = res.data.records
      total.value = res.data.totalRow
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    loading.value = false
  }
}

const resetPassword = async (row: SysUserVO) => {
  const result = await ElMessageBox.prompt('请输入新密码（为空则重置为默认 123456）', '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'password',
    inputValue: '',
  }).catch(() => null)
  if (!result) return

  const res = await resetUserPassword(row.id, { password: result.value })
  if (res.code === 0) {
    ElMessage.success('密码已重置')
  } else {
    ElMessage.error(res.message)
  }
}

onMounted(async () => {
  await Promise.all([loadRoles(), loadData()])
})
</script>

<template>
  <div class="card-panel content">
    <PageHeader title="账号管理" subtitle="管理员维护系统账号">
      <el-input v-model="query.keyword" placeholder="搜索用户名或员工信息" clearable @clear="loadData" />
      <el-select v-model="query.role" placeholder="角色" clearable @change="loadData">
        <el-option v-for="role in roleOptions" :key="role.id" :label="role.roleName" :value="role.roleCode" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
    </PageHeader>

    <el-table v-loading="loading" :data="items" stripe>
      <el-table-column prop="username" label="账号" width="180" />
      <el-table-column prop="roleCode" label="角色" width="180">
        <template #default="scope">
          <el-tag type="warning" v-if="scope.row.roleCode === 'ROLE_SYS_ADMIN'">系统超级管理员</el-tag>
          <el-tag type="success" v-else>{{ scope.row.roleName ?? scope.row.roleCode }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="employeeName" label="关联员工" />
      <el-table-column prop="employeeNo" label="工号" width="120" />
      <el-table-column prop="updatedAt" label="更新时间" width="180" />
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button v-if="canReset" size="small" @click="resetPassword(scope.row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next"
        :total="total"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>
  </div>
</template>

<style scoped>
.content {
  padding: 2rem;
}

.pagination {
  margin-top: 1.5rem;
  display: flex;
  justify-content: flex-end;
}
</style>
