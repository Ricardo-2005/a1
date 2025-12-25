<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
})

const onSubmit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login({ username: form.username, password: form.password })
    if (res.code === 0) {
      authStore.setUser(res.data)
      const roleLevel = res.data.user.roleLevel
      router.push(roleLevel >= 7 ? '/employee' : '/admin')
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login">
    <div class="login__panel card-panel">
      <div class="login__header">
        <div class="login__badge">人事</div>
        <div>
          <h1 class="login__title">人事管理平台</h1>
          <p class="muted">安全访问人事业务</p>
        </div>
      </div>
      <el-form class="login__form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" @click="onSubmit">登录</el-button>
      </el-form>
      <div class="login__note">演示模式，请使用初始化账号登录。</div>
    </div>
    <div class="login__backdrop"></div>
  </div>
</template>

<style scoped>
.login {
  min-height: 100vh;
  display: grid;
  place-items: center;
  position: relative;
  padding: 2rem;
  overflow: hidden;
}

.login__panel {
  width: min(480px, 100%);
  padding: 2.5rem;
  position: relative;
  z-index: 1;
}

.login__header {
  display: flex;
  gap: 1rem;
  align-items: center;
  margin-bottom: 1.5rem;
}

.login__badge {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: var(--accent);
  color: white;
  font-weight: 700;
  display: grid;
  place-items: center;
}

.login__title {
  margin: 0;
  font-family: 'Newsreader', 'Times New Roman', serif;
  font-size: 1.8rem;
}

.login__form {
  display: grid;
  gap: 0.75rem;
}

.login__note {
  margin-top: 1.5rem;
  font-size: 0.85rem;
  color: var(--muted);
}

.login__backdrop {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 20% 20%, rgba(245, 217, 204, 0.6), transparent 60%),
    radial-gradient(circle at 80% 10%, rgba(179, 86, 60, 0.25), transparent 55%);
}
</style>
