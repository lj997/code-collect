<template>
  <div class="app">
    <header class="header" v-if="authStore.isAuthenticated">
      <div class="header-content">
        <div class="logo" @click="router.push('/')">
          <span class="logo-icon">📋</span>
          <span class="logo-text">Code Collect</span>
        </div>
        <nav class="nav">
          <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">
            全部片段
          </router-link>
          <router-link to="/starred" class="nav-link" :class="{ active: $route.path === '/starred' }">
            ⭐ 收藏
          </router-link>
          <router-link to="/categories" class="nav-link" :class="{ active: $route.path === '/categories' }">
            分类管理
          </router-link>
          <router-link to="/snippets/new" class="nav-link nav-link-primary">
            + 新建片段
          </router-link>
        </nav>
        <div class="header-right">
          <span class="username">{{ authStore.user?.username }}</span>
          <button class="btn btn-sm btn-outline" @click="handleLogout">退出</button>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const router = useRouter()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
  padding: 12px 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-link {
  padding: 8px 16px;
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s;
}

.nav-link:hover {
  background-color: var(--bg-tertiary);
  color: var(--text-primary);
  text-decoration: none;
}

.nav-link.active {
  background-color: var(--bg-tertiary);
  color: var(--text-primary);
}

.nav-link-primary {
  background-color: var(--primary-color);
  color: white;
}

.nav-link-primary:hover {
  background-color: var(--primary-hover);
  color: white;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  color: var(--text-secondary);
  font-size: 14px;
}

.main {
  flex: 1;
  padding: 24px 0;
}
</style>
