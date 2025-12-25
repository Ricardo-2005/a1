<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { useAuthStore } from '@/store/auth'
import { createRole, deleteRole, fetchRoles, updateRole } from '@/api/role'
import { createMenu, deleteMenu, fetchMenus, updateMenu } from '@/api/menu'
import { fetchRoleMenus, saveRoleMenus } from '@/api/roleMenu'
import type { SysMenu, SysRole } from '@/api/types'

type MenuNode = SysMenu & { children?: MenuNode[] }

const authStore = useAuthStore()

const activeTab = ref('roles')

const roleLoading = ref(false)
const roles = ref<SysRole[]>([])
const roleQuery = reactive({
  keyword: '',
})

const menuLoading = ref(false)
const menus = ref<SysMenu[]>([])
const menuQuery = reactive({
  keyword: '',
})

const permsLoading = ref(false)
const selectedRoleId = ref<number>()
const menuTreeRef = ref()

const canRoleAdd = computed(() => authStore.hasPerm('system:role:add'))
const canRoleEdit = computed(() => authStore.hasPerm('system:role:edit'))
const canRoleDelete = computed(() => authStore.hasPerm('system:role:delete'))
const canMenuAdd = computed(() => authStore.hasPerm('system:menu:add'))
const canMenuEdit = computed(() => authStore.hasPerm('system:menu:edit'))
const canMenuDelete = computed(() => authStore.hasPerm('system:menu:delete'))
const canAssign = computed(() => authStore.hasPerm('system:perm:assign'))

const roleDialogVisible = ref(false)
const roleDialogTitle = ref('')
const roleFormRef = ref()
const roleForm = reactive<SysRole>({
  id: 0,
  roleName: '',
  roleCode: '',
  roleLevel: 1,
  roleSort: 1,
  status: 1,
})

const roleRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleLevel: [{ required: true, message: '请输入角色级别', trigger: 'change' }],
}

const menuDialogVisible = ref(false)
const menuDialogTitle = ref('')
const menuFormRef = ref()
const menuForm = reactive<SysMenu>({
  id: 0,
  menuName: '',
  menuType: 'C',
  path: '',
  component: '',
  perms: '',
  parentId: 0,
  orderNum: 1,
  status: 1,
})

const menuRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
}

const buildMenuTree = (list: SysMenu[]) => {
  const nodes: MenuNode[] = list.map((item) => ({ ...item, children: [] }))
  const map = new Map<number, MenuNode>()
  nodes.forEach((node) => map.set(node.id, node))
  const roots: MenuNode[] = []
  nodes.forEach((node) => {
    const parentId = node.parentId ?? 0
    if (parentId === 0) {
      roots.push(node)
    } else {
      map.get(parentId)?.children?.push(node)
    }
  })
  const sortNodes = (items: MenuNode[]) => {
    items.sort((a, b) => (a.orderNum ?? 0) - (b.orderNum ?? 0))
    items.forEach((item) => {
      if (item.children && item.children.length > 0) {
        sortNodes(item.children)
      }
    })
  }
  sortNodes(roots)
  return roots
}

const menuTree = computed(() => buildMenuTree(menus.value))

const menuTableTree = computed(() => {
  const keyword = menuQuery.keyword.trim()
  if (!keyword) {
    return menuTree.value
  }
  const hit = (item: SysMenu) =>
    item.menuName.includes(keyword) ||
    (item.perms ?? '').includes(keyword) ||
    (item.path ?? '').includes(keyword)
  const filterTree = (nodes: MenuNode[]): MenuNode[] => {
    const result: MenuNode[] = []
    nodes.forEach((node) => {
      const children = node.children ? filterTree(node.children) : []
      if (hit(node) || children.length > 0) {
        result.push({ ...node, children })
      }
    })
    return result
  }
  return filterTree(menuTree.value)
})

const menuParentOptions = computed(() => {
  const options: Array<{ id: number; label: string }> = [{ id: 0, label: '顶级目录' }]
  const walk = (nodes: MenuNode[], depth: number) => {
    nodes.forEach((node) => {
      const prefix = depth > 0 ? `${'--'.repeat(depth)} ` : ''
      options.push({ id: node.id, label: `${prefix}${node.menuName}` })
      if (node.children && node.children.length > 0) {
        walk(node.children, depth + 1)
      }
    })
  }
  walk(menuTree.value, 0)
  return options
})

const expandedMenuIds = computed(() => menus.value.map((menu) => menu.id))

const menuTypeLabel = (type: SysMenu['menuType']) => {
  if (type === 'M') return '目录'
  if (type === 'F') return '按钮'
  return '菜单'
}

const loadRoles = async () => {
  roleLoading.value = true
  try {
    const res = await fetchRoles(roleQuery)
    if (res.code === 0) {
      roles.value = res.data
      if (!selectedRoleId.value && roles.value.length > 0) {
        selectedRoleId.value = roles.value[0].id
      }
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    roleLoading.value = false
  }
}

const loadMenus = async () => {
  menuLoading.value = true
  try {
    const res = await fetchMenus()
    if (res.code === 0) {
      menus.value = res.data
      await nextTick()
      if (selectedRoleId.value) {
        await loadRoleMenus()
      }
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    menuLoading.value = false
  }
}

const loadRoleMenus = async () => {
  if (!selectedRoleId.value) {
    return
  }
  permsLoading.value = true
  try {
    const res = await fetchRoleMenus(selectedRoleId.value)
    if (res.code === 0) {
      await nextTick()
      menuTreeRef.value?.setCheckedKeys(res.data ?? [])
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    permsLoading.value = false
  }
}

const refreshAll = async () => {
  await Promise.all([loadRoles(), loadMenus()])
}

const openRoleCreate = () => {
  roleForm.id = 0
  roleForm.roleName = ''
  roleForm.roleCode = ''
  roleForm.roleLevel = 1
  roleForm.roleSort = 1
  roleForm.status = 1
  roleDialogTitle.value = '新增角色'
  roleDialogVisible.value = true
}

const openRoleEdit = (row: SysRole) => {
  roleForm.id = row.id
  roleForm.roleName = row.roleName
  roleForm.roleCode = row.roleCode
  roleForm.roleLevel = row.roleLevel
  roleForm.roleSort = row.roleSort
  roleForm.status = row.status
  roleDialogTitle.value = '编辑角色'
  roleDialogVisible.value = true
}

const submitRole = async () => {
  await roleFormRef.value?.validate()
  const payload = {
    roleName: roleForm.roleName,
    roleCode: roleForm.roleCode,
    roleLevel: roleForm.roleLevel,
    roleSort: roleForm.roleSort,
    status: roleForm.status,
  }
  const res = roleForm.id
    ? await updateRole(roleForm.id, payload)
    : await createRole(payload)
  if (res.code === 0) {
    ElMessage.success('角色已保存')
    roleDialogVisible.value = false
    await loadRoles()
  } else {
    ElMessage.error(res.message)
  }
}

const removeRole = async (row: SysRole) => {
  await ElMessageBox.confirm(`确定删除角色 ${row.roleName} 吗？`, '提示', { type: 'warning' })
  const res = await deleteRole(row.id)
  if (res.code === 0) {
    ElMessage.success('角色已删除')
    await loadRoles()
  } else {
    ElMessage.error(res.message)
  }
}

const openMenuCreate = () => {
  menuForm.id = 0
  menuForm.menuName = ''
  menuForm.menuType = 'C'
  menuForm.path = ''
  menuForm.component = ''
  menuForm.perms = ''
  menuForm.parentId = 0
  menuForm.orderNum = 1
  menuForm.status = 1
  menuDialogTitle.value = '新增菜单'
  menuDialogVisible.value = true
}

const openMenuEdit = (row: SysMenu) => {
  menuForm.id = row.id
  menuForm.menuName = row.menuName
  menuForm.menuType = row.menuType
  menuForm.path = row.path ?? ''
  menuForm.component = row.component ?? ''
  menuForm.perms = row.perms ?? ''
  menuForm.parentId = row.parentId ?? 0
  menuForm.orderNum = row.orderNum ?? 1
  menuForm.status = row.status ?? 1
  menuDialogTitle.value = '编辑菜单'
  menuDialogVisible.value = true
}

const submitMenu = async () => {
  await menuFormRef.value?.validate()
  const payload = {
    menuName: menuForm.menuName,
    menuType: menuForm.menuType,
    path: menuForm.path || undefined,
    component: menuForm.component || undefined,
    perms: menuForm.perms || undefined,
    parentId: menuForm.parentId ?? 0,
    orderNum: menuForm.orderNum ?? 0,
    status: menuForm.status ?? 1,
  }
  const res = menuForm.id
    ? await updateMenu(menuForm.id, payload)
    : await createMenu(payload)
  if (res.code === 0) {
    ElMessage.success('菜单已保存')
    menuDialogVisible.value = false
    await loadMenus()
  } else {
    ElMessage.error(res.message)
  }
}

const removeMenu = async (row: SysMenu) => {
  await ElMessageBox.confirm(`确定删除菜单 ${row.menuName} 吗？`, '提示', { type: 'warning' })
  const res = await deleteMenu(row.id)
  if (res.code === 0) {
    ElMessage.success('菜单已删除')
    await loadMenus()
  } else {
    ElMessage.error(res.message)
  }
}

const saveRoleMenu = async () => {
  if (!selectedRoleId.value) {
    ElMessage.warning('请先选择角色')
    return
  }
  const checkedKeys = (menuTreeRef.value?.getCheckedKeys?.() ?? []) as number[]
  const halfKeys = (menuTreeRef.value?.getHalfCheckedKeys?.() ?? []) as number[]
  const menuIds = Array.from(new Set([...checkedKeys, ...halfKeys]))
  const res = await saveRoleMenus(selectedRoleId.value, menuIds)
  if (res.code === 0) {
    ElMessage.success('权限已保存')
  } else {
    ElMessage.error(res.message)
  }
}

watch(selectedRoleId, async (value) => {
  if (!value) return
  await loadRoleMenus()
})

onMounted(async () => {
  await refreshAll()
})
</script>

<template>
  <div class="card-panel content">
    <PageHeader title="权限管理" subtitle="角色管理、菜单管理与权限分配">
      <el-button @click="refreshAll">刷新数据</el-button>
    </PageHeader>

    <el-tabs v-model="activeTab" class="tabs">
      <el-tab-pane label="角色管理" name="roles">
        <div class="toolbar">
          <el-input
            v-model="roleQuery.keyword"
            placeholder="搜索角色名称或编码"
            clearable
            @clear="loadRoles"
            @keyup.enter="loadRoles"
          />
          <el-button type="primary" @click="loadRoles">查询</el-button>
          <el-button v-if="canRoleAdd" @click="openRoleCreate">新增角色</el-button>
        </div>

        <el-table v-loading="roleLoading" :data="roles" stripe>
          <el-table-column prop="roleName" label="角色名称" />
          <el-table-column prop="roleCode" label="角色编码" width="180" />
          <el-table-column prop="roleLevel" label="层级" width="80" />
          <el-table-column prop="roleSort" label="排序" width="80" />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="scope">
              <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
              <el-tag v-else type="info">停用</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button v-if="canRoleEdit" size="small" @click="openRoleEdit(scope.row)">编辑</el-button>
              <el-button
                v-if="canRoleDelete"
                size="small"
                type="danger"
                @click="removeRole(scope.row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="菜单管理" name="menus">
        <div class="toolbar">
          <el-input
            v-model="menuQuery.keyword"
            placeholder="搜索菜单名称、路径或权限标识"
            clearable
            @clear="loadMenus"
            @keyup.enter="loadMenus"
          />
          <el-button type="primary" @click="loadMenus">查询</el-button>
          <el-button v-if="canMenuAdd" @click="openMenuCreate">新增菜单</el-button>
        </div>

        <el-table
          v-loading="menuLoading"
          :data="menuTableTree"
          row-key="id"
          default-expand-all
          stripe
          :tree-props="{ children: 'children' }"
        >
          <el-table-column prop="menuName" label="菜单名称" />
          <el-table-column prop="menuType" label="类型" width="100">
            <template #default="scope">
              <el-tag>{{ menuTypeLabel(scope.row.menuType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="path" label="路由" />
          <el-table-column prop="perms" label="权限标识" />
          <el-table-column prop="orderNum" label="排序" width="80" />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="scope">
              <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
              <el-tag v-else type="info">停用</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button v-if="canMenuEdit" size="small" @click="openMenuEdit(scope.row)">编辑</el-button>
              <el-button
                v-if="canMenuDelete"
                size="small"
                type="danger"
                @click="removeMenu(scope.row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="权限分配" name="perms">
        <div class="toolbar">
          <el-select v-model="selectedRoleId" placeholder="选择角色" style="min-width: 260px">
            <el-option v-for="role in roles" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
          <el-button type="primary" :disabled="!selectedRoleId" @click="loadRoleMenus">加载权限</el-button>
          <el-button v-if="canAssign" :disabled="!selectedRoleId" @click="saveRoleMenu">保存权限</el-button>
        </div>

        <el-card class="tree-card" shadow="never">
          <el-tree
            ref="menuTreeRef"
            v-loading="permsLoading"
            :data="menuTree"
            node-key="id"
            show-checkbox
            default-expand-all
            :default-expanded-keys="expandedMenuIds"
            :props="{ label: 'menuName', children: 'children' }"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="roleDialogVisible" :title="roleDialogTitle" width="520px">
      <el-form ref="roleFormRef" :model="roleForm" :rules="roleRules" label-position="top">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" />
        </el-form-item>
        <el-form-item label="角色级别" prop="roleLevel">
          <el-input-number v-model="roleForm.roleLevel" :min="1" :max="99" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="roleForm.roleSort" :min="1" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="roleForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRole">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="menuDialogVisible" :title="menuDialogTitle" width="560px">
      <el-form ref="menuFormRef" :model="menuForm" :rules="menuRules" label-position="top">
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="menuForm.menuName" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="menuForm.menuType">
            <el-radio label="M">目录</el-radio>
            <el-radio label="C">菜单</el-radio>
            <el-radio label="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="上级菜单">
          <el-select v-model="menuForm.parentId" placeholder="选择上级菜单">
            <el-option v-for="item in menuParentOptions" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="路由">
          <el-input v-model="menuForm.path" placeholder="例如 /admin/employees" />
        </el-form-item>
        <el-form-item label="组件">
          <el-input v-model="menuForm.component" placeholder="例如 EmployeeView" />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="menuForm.perms" placeholder="例如 system:employee:list" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="menuForm.orderNum" :min="1" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="menuForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMenu">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.content {
  padding: 2rem;
}

.tabs {
  margin-top: 1rem;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.tree-card {
  margin-top: 0.5rem;
}
</style>
