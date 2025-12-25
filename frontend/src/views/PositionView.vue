<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import {
  createPosition,
  deletePosition,
  fetchPositions,
  updatePosition,
} from '@/api/position'
import type { Position } from '@/api/types'
import { useAuthStore } from '@/store/auth'

const loading = ref(false)
const items = ref<Position[]>([])
const total = ref(0)
const authStore = useAuthStore()

const query = reactive({
  keyword: '',
  page: 1,
  size: 10,
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = reactive<Position>({
  id: 0,
  name: '',
  code: '',
  description: '',
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
}

const canAdd = computed(() => authStore.hasPerm('system:position:add'))
const canEdit = computed(() => authStore.hasPerm('system:position:edit'))
const canDelete = computed(() => authStore.hasPerm('system:position:delete'))

const loadData = async () => {
  loading.value = true
  try {
    const res = await fetchPositions(query)
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
  form.name = ''
  form.code = ''
  form.description = ''
}

const openCreate = () => {
  resetForm()
  dialogTitle.value = '新增岗位'
  dialogVisible.value = true
}

const openEdit = (row: Position) => {
  form.id = row.id
  form.name = row.name
  form.code = row.code
  form.description = row.description ?? ''
  dialogTitle.value = '编辑岗位'
  dialogVisible.value = true
}

const submit = async () => {
  await formRef.value?.validate()
  const payload = {
    name: form.name,
    code: form.code,
    description: form.description,
  }
  const res = form.id ? await updatePosition(form.id, payload) : await createPosition(payload)
  if (res.code === 0) {
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await loadData()
  } else {
    ElMessage.error(res.message)
  }
}

const removeItem = async (row: Position) => {
  await ElMessageBox.confirm(`确定删除 ${row.name} 吗？`, '提示', { type: 'warning' })
  const res = await deletePosition(row.id)
  if (res.code === 0) {
    ElMessage.success('删除成功')
    await loadData()
  } else {
    ElMessage.error(res.message)
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="card-panel content">
    <PageHeader title="岗位管理" subtitle="维护岗位与职责">
      <el-input v-model="query.keyword" placeholder="搜索名称或编码" clearable @clear="loadData" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button v-if="canAdd" @click="openCreate">新增岗位</el-button>
    </PageHeader>

    <el-table v-loading="loading" :data="items" stripe>
      <el-table-column prop="name" label="岗位名称" />
      <el-table-column prop="code" label="岗位编码" width="140" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button v-if="canEdit" size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button v-if="canDelete" size="small" type="danger" @click="removeItem(scope.row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" />
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
