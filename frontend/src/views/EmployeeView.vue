<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { fetchAllDepartments } from '@/api/department'
import { fetchAllPositions } from '@/api/position'
import {
  createEmployee,
  fetchEmployees,
  updateEmployee,
  type EmployeeCreatePayload,
  type EmployeeUpdatePayload,
} from '@/api/employee'
import { createLeave, fetchLeaves, restoreLeave } from '@/api/leave'
import type { Department, EmployeeVO, Position } from '@/api/types'
import { useAuthStore } from '@/store/auth'

const loading = ref(false)
const items = ref<EmployeeVO[]>([])
const total = ref(0)
const authStore = useAuthStore()

const departments = ref<Department[]>([])
const positions = ref<Position[]>([])

const query = reactive({
  keyword: '',
  departmentId: undefined as number | undefined,
  positionId: undefined as number | undefined,
  status: '',
  page: 1,
  size: 10,
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = reactive({
  id: 0,
  employeeNo: '',
  name: '',
  gender: '',
  phone: '',
  email: '',
  departmentId: undefined as number | undefined,
  positionId: undefined as number | undefined,
  hireDate: '',
})

const rules = {
  employeeNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  positionId: [{ required: true, message: '请选择岗位', trigger: 'change' }],
  hireDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
}

const canAdd = computed(() => authStore.hasPerm('system:employee:add'))
const canEdit = computed(() => authStore.hasPerm('system:employee:edit'))
const canLeave = computed(() => authStore.hasPerm('system:leave:add'))
const canRestore = computed(() => authStore.hasPerm('system:leave:restore'))

const loadOptions = async () => {
  const [deptRes, positionRes] = await Promise.all([fetchAllDepartments(), fetchAllPositions()])
  if (deptRes.code === 0) {
    departments.value = deptRes.data
  }
  if (positionRes.code === 0) {
    positions.value = positionRes.data
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await fetchEmployees(query)
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

const resetForm = () => {
  form.id = 0
  form.employeeNo = ''
  form.name = ''
  form.gender = ''
  form.phone = ''
  form.email = ''
  form.departmentId = undefined
  form.positionId = undefined
  form.hireDate = ''
}

const openCreate = () => {
  resetForm()
  dialogTitle.value = '新增员工'
  dialogVisible.value = true
}

const openEdit = (row: EmployeeVO) => {
  form.id = row.id
  form.employeeNo = row.employeeNo
  form.name = row.name
  form.gender = row.gender ?? ''
  form.phone = row.phone ?? ''
  form.email = row.email ?? ''
  form.departmentId = row.departmentId
  form.positionId = row.positionId
  form.hireDate = row.hireDate
  dialogTitle.value = '编辑员工'
  dialogVisible.value = true
}

const submit = async () => {
  await formRef.value?.validate()
  if (form.id) {
    const payload: EmployeeUpdatePayload = {
      name: form.name,
      gender: form.gender,
      phone: form.phone,
      email: form.email || undefined,
      departmentId: form.departmentId as number,
      positionId: form.positionId as number,
    }
    const res = await updateEmployee(form.id, payload)
    if (res.code === 0) {
      ElMessage.success('更新成功')
      dialogVisible.value = false
      await loadData()
      return
    }
    ElMessage.error(res.message)
    return
  }

  const payload: EmployeeCreatePayload = {
    employeeNo: form.employeeNo,
    name: form.name,
    gender: form.gender,
    phone: form.phone,
    email: form.email || undefined,
    departmentId: form.departmentId as number,
    positionId: form.positionId as number,
    hireDate: form.hireDate,
  }
  const res = await createEmployee(payload)
  if (res.code === 0) {
    ElMessage.success('创建成功')
    dialogVisible.value = false
    await loadData()
  } else {
    ElMessage.error(res.message)
  }
}

const formatDate = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const removeItem = async (row: EmployeeVO) => {
  const result = await ElMessageBox.prompt('请输入离职原因（可选）', '办理离职', {
    confirmButtonText: '确认离职',
    cancelButtonText: '取消',
    inputPlaceholder: '可为空',
  }).catch(() => null)
  if (!result) {
    return
  }
  const res = await createLeave({
    employeeId: row.id,
    leaveDate: formatDate(new Date()),
    reason: result.value || undefined,
  })
  if (res.code === 0) {
    ElMessage.success('已办理离职')
    await loadData()
  } else {
    ElMessage.error(res.message)
  }
}

const restoreItem = async (row: EmployeeVO) => {
  const res = await fetchLeaves({ employeeId: row.id, page: 1, size: 1 })
  if (res.code !== 0) {
    ElMessage.error(res.message)
    return
  }
  const record = res.data.records?.[0]
  if (!record) {
    ElMessage.warning('未找到离职记录')
    return
  }
  const restoreRes = await restoreLeave(record.id)
  if (restoreRes.code === 0) {
    ElMessage.success('已重新入职')
    await loadData()
  } else {
    ElMessage.error(restoreRes.message)
  }
}

onMounted(async () => {
  await loadOptions()
  await loadData()
})
</script>

<template>
  <div class="card-panel content">
    <PageHeader title="员工管理" subtitle="管理员工信息与状态">
      <el-input v-model="query.keyword" placeholder="搜索姓名或工号" clearable @clear="loadData" />
      <el-select v-model="query.departmentId" placeholder="部门" clearable @change="loadData">
        <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
      </el-select>
      <el-select v-model="query.positionId" placeholder="岗位" clearable @change="loadData">
        <el-option v-for="pos in positions" :key="pos.id" :label="pos.name" :value="pos.id" />
      </el-select>
      <el-select v-model="query.status" placeholder="状态" clearable @change="loadData">
        <el-option label="在职" value="ACTIVE" />
        <el-option label="离职" value="LEFT" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button v-if="canAdd" @click="openCreate">新增员工</el-button>
    </PageHeader>

    <el-table v-loading="loading" :data="items" stripe>
      <el-table-column prop="employeeNo" label="工号" width="120" />
      <el-table-column prop="name" label="姓名" width="140" />
      <el-table-column prop="departmentName" label="部门" />
      <el-table-column prop="positionName" label="岗位" />
      <el-table-column prop="hireDate" label="入职日期" width="130" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 'ACTIVE'" type="success">在职</el-tag>
          <el-tag v-else type="info">离职</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button v-if="canEdit" size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button
            v-if="scope.row.status === 'LEFT' ? canRestore : canLeave"
            size="small"
            :type="scope.row.status === 'LEFT' ? 'success' : 'danger'"
            @click="scope.row.status === 'LEFT' ? restoreItem(scope.row) : removeItem(scope.row)"
          >
            {{ scope.row.status === 'LEFT' ? '重新入职' : '离职' }}
          </el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="工号" prop="employeeNo">
          <el-input v-model="form.employeeNo" :disabled="form.id > 0" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-input v-model="form.gender" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="部门" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="选择部门">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="岗位" prop="positionId">
          <el-select v-model="form.positionId" placeholder="选择岗位">
            <el-option v-for="pos in positions" :key="pos.id" :label="pos.name" :value="pos.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期" prop="hireDate">
          <el-date-picker v-model="form.hireDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
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
