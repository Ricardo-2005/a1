<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { fetchEmployees } from '@/api/employee'
import { fetchAllPositions } from '@/api/position'
import { createTransfer, fetchTransfers } from '@/api/transfer'
import type { EmployeeVO, Position, PositionTransferVO } from '@/api/types'
import { useAuthStore } from '@/store/auth'

const loading = ref(false)
const items = ref<PositionTransferVO[]>([])
const total = ref(0)
const authStore = useAuthStore()

const employees = ref<EmployeeVO[]>([])
const positions = ref<Position[]>([])

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
  toPositionId: undefined as number | undefined,
  transferDate: '',
  reason: '',
})

const rules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  toPositionId: [{ required: true, message: '请选择调入岗位', trigger: 'change' }],
  transferDate: [{ required: true, message: '请选择调动日期', trigger: 'change' }],
}

const selectedEmployee = computed(() => employees.value.find((emp) => emp.id === form.employeeId))
const fromPositionName = computed(() => selectedEmployee.value?.positionName ?? '-')
const canAdd = computed(() => authStore.hasPerm('system:transfer:add'))

const loadEmployees = async () => {
  const res = await fetchEmployees({ status: 'ACTIVE', page: 1, size: 200 })
  if (res.code === 0) {
    employees.value = res.data.records
  } else {
    ElMessage.error(res.message)
  }
}

const loadPositions = async () => {
  const res = await fetchAllPositions()
  if (res.code === 0) {
    positions.value = res.data
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
    const res = await fetchTransfers(params)
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
  form.toPositionId = undefined
  form.transferDate = ''
  form.reason = ''
}

const openCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const submit = async () => {
  await formRef.value?.validate()
  if (selectedEmployee.value && selectedEmployee.value.positionId === form.toPositionId) {
    ElMessage.warning('调入岗位与当前岗位相同')
    return
  }
  const payload = {
    employeeId: form.employeeId as number,
    toPositionId: form.toPositionId as number,
    transferDate: form.transferDate,
    reason: form.reason || undefined,
  }
  const res = await createTransfer(payload)
  if (res.code === 0) {
    ElMessage.success('调动记录已保存')
    dialogVisible.value = false
    await loadEmployees()
    await loadData()
  } else {
    ElMessage.error(res.message)
  }
}

onMounted(async () => {
  await Promise.all([loadEmployees(), loadPositions()])
  await loadData()
})
</script>

<template>
  <div class="card-panel content">
    <PageHeader title="岗位调动" subtitle="记录员工岗位调动">
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
      <el-button v-if="canAdd" @click="openCreate">记录调动</el-button>
    </PageHeader>

    <el-table v-loading="loading" :data="items" stripe>
      <el-table-column prop="employeeName" label="员工" width="140" />
      <el-table-column prop="fromPositionName" label="调出岗位" />
      <el-table-column prop="toPositionName" label="调入岗位" />
      <el-table-column prop="transferDate" label="调动日期" width="130" />
      <el-table-column prop="reason" label="调动原因" />
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

    <el-dialog v-model="dialogVisible" title="记录岗位调动" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="员工" prop="employeeId">
          <el-select v-model="form.employeeId" placeholder="选择员工">
            <el-option
              v-for="emp in employees"
              :key="emp.id"
              :label="`${emp.name}（${emp.employeeNo}）`"
              :value="emp.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="当前岗位">
          <el-input :model-value="fromPositionName" disabled />
        </el-form-item>
        <el-form-item label="调入岗位" prop="toPositionId">
          <el-select v-model="form.toPositionId" placeholder="选择岗位">
            <el-option v-for="pos in positions" :key="pos.id" :label="pos.name" :value="pos.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="调动日期" prop="transferDate">
          <el-date-picker v-model="form.transferDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="调动原因">
          <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="可选填写" />
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
