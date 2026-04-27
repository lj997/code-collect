<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1 class="logo">📋 Code Collect</h1>
        <p class="subtitle">登录你的代码片段收藏工具</p>
      </div>
      
      <div v-if="error" class="alert alert-error">{{ error }}</div>
      
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input 
            type="text" 
            v-model="form.username" 
            class="form-input"
            placeholder="请输入用户名"
            required
          >
        </div>
        
        <div class="form-group">
          <label class="form-label">密码</label>
          <input 
            type="password" 
            v-model="form.password" 
            class="form-input"
            placeholder="请输入密码"
            required
          >
        </div>
        
        <button type="submit" class="btn btn-primary btn-lg w-full" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <div class="login-footer">
        还没有账户？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  username: '',
  password: ''
})
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  loading.value = true
  error.value = ''
  
  const result = await authStore.handleLogin(form.value)
  
  if (result.success) {
    router.push('/')
  } else {
    error.value = result.message
  }
  
  loading.value = false
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-card {
  background-color: var(--bg-secondary);
  border-radius: 12px;
  padding: 32px;
  width: 100%;
  max-width: 400px;
  border: 1px solid var(--border-color);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  font-size: 24px;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.subtitle {
  color: var(--text-muted);
  font-size: 14px;
}

.w-full {
  width: 100%;
}

.login-footer {
  margin-top: 24px;
  text-align: center;
  color: var(--text-muted);
  font-size: 14px;
}

.login-footer a {
  color: var(--primary-color);
  margin-left: 4px;
}
</style>
