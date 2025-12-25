<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { fetchEmployees } from '@/api/employee'
import { createLeave, fetchLeaves, restoreLeave } from '@/api/leave'
import type { EmployeeLeaveVO, EmployeeVO } from '@/api/types'
import { useAuthStore } from '@/store/auth'

const loading = ref(false)
const items = ref<EmployeeLeaveVO[]>([])
const total = ref(0)
const authStore = useAuthStore()

const employees = ref<EmployeeVO[]>([])
const activeEmployees = computed(() => employees.value.filter((emp) => emp.status === 'ACTIVE'))
const employeeMap = computed(() => {
  const map = new Map<number, string>()
  employees.value.forEach((emp) => {
    map.set(emp.id, `${emp.name}（${emp.employeeNo}）`)
  })
  return map
})

const query = reactive({
  employeeId: undefined as number | undefined,
  dateRange: [] as string[],
  page: 1,
  size: 10,
})

const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({
  employeeId: undefined as number | undefined,
  leaveDate: '',
  reason: '',
})

const rules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  leaveDate: [{ required: true, message: '请选择离职日期', trigger: 'change' }],
}

const canAdd = computed(() => authStore.hasPerm('system:leave:add'))
const canRestore = computed(() => authStore.hasPerm('system:leave:restore'))

const loadEmployees = async () => {
  const res = await fetchEmployees({ page: 1, size: 200 })
  if (res.code === 0) {
    employees.value = res.data.records
  } else {
    ElMessage.error(res.message)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      employeeId: query.employeeId,
      startDate: query.dateRange?.[0],
      endDate: query.dateRange?.[1],
      page: query.page,
      size: query.size,
    }
    const res = await fetchLeaves(params)
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
  form.employeeId = undefined
  form.leaveDate = ''
  form.reason = ''
}

const formatDate = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const openCreate = () => {
  resetForm()
  form.leaveDate = formatDate(new Date())
  dialogVisible.value = true
}

const submit = async () => {
  await formRef.value?.validate()
  const payload = {
    employeeId: form.employeeId as number,
    leaveDate: form.leaveDate,
    reason: form.reason || undefined,
  }
  const res = await createLeave(payload)
  if (res.code === 0) {
    ElMessage.success('离职登记成功')
    dialogVisible.value = false
    await Promise.all([loadEmployees(), loadData()])
  } else {
    ElMessage.error(res.message)
  }
}

const restoreItem = async (row: EmployeeLeaveVO) => {
  const res = await restoreLeave(row.id)
  if (res.code === 0) {
    ElMessage.success('已恢复在职')
    await Promise.all([loadEmployees(), loadData()])
  } else {
    ElMessage.error(res.message)
  }
}

onMounted(async () => {
  await Promise.all([loadEmployees(), loadData()])
})
</script>

<template>
  <div class="card-panel content">
    <PageHeader title="员工离职" subtitle="记录离职与状态变更">
      <el-select v-model="query.employeeId" placeholder="员工" clearable @change="loadData">
        <el-option
          v-for="emp in employees"
          :key="emp.id"
          :label="`${emp.name}（${emp.employeeNo}）`"
          :value="emp.id"
        />
      </el-select>
      <el-date-picker
        v-model="query.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        @change="loadData"
      />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button v-if="canAdd" @click="openCreate">登记离职</el-button>
    </PageHeader>

    <el-alert
      title="离职员工将禁止再次调动，请确认离职信息后提交。"
      type="warning"
      show-icon
      class="notice"
    />

    <el-table v-loading="loading" :data="items" stripe>
      <el-table-column label="员工" width="180">
        <template #default="scope">
          {{
            scope.row.employeeName
              ?? employeeMap.get(scope.row.employeeId)
              ?? (scope.row.employeeNo ? `员工 ${scope.row.employeeNo}` : scope.row.employeeId)
          }}
        </template>
      </el-table-column>
      <el-table-column prop="leaveDate" label="离职日期" width="140" />
      <el-table-column prop="reason" label="离职原因" />
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button v-if="canRestore" size="small" @click="restoreItem(scope.row)">恢复在职</el-button>
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

    <el-dialog v-model="dialogVisible" title="登记离职" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="员工" prop="employeeId">
          <el-select v-model="form.employeeId" placeholder="选择员工">
            <el-option
              v-for="emp in activeEmployees"
              :key="emp.id"
              :label="`${emp.name}（${emp.employeeNo}）`"
              :value="emp.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="离职日期" prop="leaveDate">
          <el-date-picker v-model="form.leaveDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="离职原因">
          <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="可选填写" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.content {
  padding: 2rem;
}

.notice {
  margin-bottom: 1rem;
}

.pagination {
  margin-top: 1.5rem;
  display: flex;
  justify-content: flex-end;
}
</style>
