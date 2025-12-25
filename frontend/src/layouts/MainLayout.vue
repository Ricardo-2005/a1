<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/app'
import { useAuthStore } from '@/store/auth'
import type { MenuItem } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()

const activePath = computed(() => route.path)

type MenuNode = MenuItem & { children?: MenuNode[] }

const buildMenuTree = (menus: MenuItem[]) => {
  const nodes = menus
    .filter((menu) => menu.menuType !== 'F' && menu.status !== 0)
    .map((menu) => ({ ...menu, children: [] as MenuNode[] }))
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
  const sortNodes = (list: MenuNode[]) => {
    list.sort((a, b) => (a.orderNum ?? 0) - (b.orderNum ?? 0))
    list.forEach((item) => {
      if (item.children && item.children.length > 0) {
        sortNodes(item.children)
      }
    })
  }
  sortNodes(roots)
  return roots
}

const menuTree = computed(() => buildMenuTree(authStore.menus))

const headerTitle = computed(() => {
  if (authStore.roleLevel >= 7) {
    return '员工中心'
  }
  if (authStore.roleLevel <= 3) {
    return '系统控制台'
  }
  return '管理控制台'
})

const headerSubtitle = computed(() => {
  if (authStore.roleLevel >= 7) {
    return '仅查看个人信息'
  }
  if (authStore.roleLevel <= 3) {
    return '全局视图与权限管理'
  }
  return '授权模块与业务处理'
})

const goLogin = () => {
  authStore.clearUser()
  router.push('/login')
}
</script>

<template>
  <div class="page-shell">
    <el-container class="layout">
      <el-aside :width="appStore.sidebarCollapsed ? '84px' : '240px'" class="aside">
        <div class="brand">
          <div class="brand__mark">人事</div>
          <div v-if="!appStore.sidebarCollapsed" class="brand__text">
            <div class="brand__title">人力资源</div>
            <div class="brand__subtitle">管理系统</div>
          </div>
        </div>
        <el-menu
          :default-active="activePath"
          class="menu"
          router
          :collapse="appStore.sidebarCollapsed"
          :collapse-transition="false"
        >
          <template v-for="item in menuTree" :key="item.id">
            <el-sub-menu v-if="item.children && item.children.length > 0" :index="item.path ?? String(item.id)">
              <template #title>
                <span>{{ item.menuName }}</span>
              </template>
              <el-menu-item
                v-for="child in item.children"
                :key="child.id"
                :index="child.path ?? String(child.id)"
              >
                <span>{{ child.menuName }}</span>
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="item.path ?? String(item.id)">
              <span>{{ item.menuName }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div>
            <div class="header__title">{{ headerTitle }}</div>
            <div class="header__subtitle">{{ headerSubtitle }}</div>
          </div>
          <div class="header__actions">
            <el-button text @click="appStore.toggleSidebar()">
              {{ appStore.sidebarCollapsed ? '展开' : '收起' }}
            </el-button>
            <el-button type="primary" @click="goLogin">退出</el-button>
          </div>
        </el-header>
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
}

.aside {
  padding: 1.5rem 1rem;
  background: linear-gradient(180deg, #fef7ef 0%, #f1e4d7 100%);
  border-right: 1px solid var(--stroke);
}

.brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.brand__mark {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: var(--accent);
  color: white;
  display: grid;
  place-items: center;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.brand__title {
  font-family: 'Newsreader', 'Times New Roman', serif;
  font-size: 1.1rem;
}

.brand__subtitle {
  font-size: 0.8rem;
  color: var(--muted);
}

.menu {
  border: none;
  background: transparent;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem 2rem;
  background: transparent;
}

.header__title {
  font-family: 'Newsreader', 'Times New Roman', serif;
  font-size: 1.4rem;
}

.header__subtitle {
  color: var(--muted);
  font-size: 0.9rem;
}

.header__actions {
  display: flex;
  gap: 0.75rem;
}

.main {
  padding: 0 2rem 2rem;
}
</style>
