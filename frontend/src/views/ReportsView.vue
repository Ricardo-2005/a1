<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import {
  fetchHireReport,
  fetchLeaveReport,
  fetchTransferReport,
  type HireReportRow,
  type LeaveReportRow,
  type TransferReportRow,
} from '@/api/report'

const activeTab = ref('hire')
const loading = ref(false)

const query = reactive({
  startDate: '',
  endDate: '',
})

const hireRows = ref<HireReportRow[]>([])
const leaveRows = ref<LeaveReportRow[]>([])
const transferRows = ref<TransferReportRow[]>([])

const loadReport = async () => {
  loading.value = true
  try {
    const params = {
      startDate: query.startDate || undefined,
      endDate: query.endDate || undefined,
    }
    if (activeTab.value === 'hire') {
      const res = await fetchHireReport(params)
      if (res.code === 0) {
        hireRows.value = res.data
      } else {
        ElMessage.error(res.message)
      }
    }
    if (activeTab.value === 'leave') {
      const res = await fetchLeaveReport(params)
      if (res.code === 0) {
        leaveRows.value = res.data
      } else {
        ElMessage.error(res.message)
      }
    }
    if (activeTab.value === 'transfer') {
      const res = await fetchTransferReport(params)
      if (res.code === 0) {
        transferRows.value = res.data
      } else {
        ElMessage.error(res.message)
      }
    }
  } finally {
    loading.value = false
  }
}

watch(activeTab, () => {
  loadReport()
})

onMounted(() => {
  loadReport()
})
</script>

<template>
  <div class="card-panel content">
    <PageHeader title="报表管理" subtitle="按时间查看入职、调动与离职">
      <el-date-picker
        v-model="query.startDate"
        type="date"
        value-format="YYYY-MM-DD"
        placeholder="开始日期"
      />
      <el-date-picker
        v-model="query.endDate"
        type="date"
        value-format="YYYY-MM-DD"
        placeholder="结束日期"
      />
      <el-button type="primary" @click="loadReport">刷新</el-button>
    </PageHeader>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="新聘员工" name="hire">
        <el-table v-loading="loading" :data="hireRows" stripe>
          <el-table-column prop="employeeNo" label="工号" width="120" />
          <el-table-column prop="name" label="姓名" width="140" />
          <el-table-column prop="departmentName" label="部门" />
          <el-table-column prop="positionName" label="岗位" />
          <el-table-column prop="hireDate" label="入职日期" width="140" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="离职员工" name="leave">
        <el-table v-loading="loading" :data="leaveRows" stripe>
          <el-table-column prop="employeeNo" label="工号" width="120" />
          <el-table-column prop="name" label="姓名" width="140" />
          <el-table-column prop="departmentName" label="部门" />
          <el-table-column prop="positionName" label="岗位" />
          <el-table-column prop="leaveDate" label="离职日期" width="140" />
          <el-table-column prop="reason" label="原因" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="岗位调动" name="transfer">
        <el-table v-loading="loading" :data="transferRows" stripe>
          <el-table-column prop="employeeNo" label="工号" width="120" />
          <el-table-column prop="name" label="姓名" width="140" />
          <el-table-column prop="fromPositionName" label="原岗位" />
          <el-table-column prop="toPositionName" label="现岗位" />
          <el-table-column prop="transferDate" label="调动日期" width="140" />
          <el-table-column prop="reason" label="原因" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.content {
  padding: 2rem;
}
</style>
