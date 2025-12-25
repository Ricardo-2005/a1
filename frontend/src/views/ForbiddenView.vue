<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="card-panel content">
    <h1 class="title">403 无权限</h1>
    <p class="subtitle">当前账号没有访问此页面的权限。</p>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>

<style scoped>
.content {
  padding: 2rem;
}

.title {
  margin: 0 0 0.5rem;
  font-size: 1.6rem;
}

.subtitle {
  margin: 0 0 1.5rem;
  color: var(--muted);
}
</style>
