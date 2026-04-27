import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)

  async function handleLogin(credentials) {
    try {
      const response = await login(credentials)
      token.value = response.token
      user.value = {
        id: response.id,
        username: response.username,
        email: response.email
      }
      
      localStorage.setItem('token', response.token)
      localStorage.setItem('user', JSON.stringify(user.value))
      
      return { success: true }
    } catch (error) {
      return { success: false, message: error.response?.data || '登录失败' }
    }
  }

  async function handleRegister(data) {
    try {
      const response = await register(data)
      token.value = response.token
      user.value = {
        id: response.id,
        username: response.username,
        email: response.email
      }
      
      localStorage.setItem('token', response.token)
      localStorage.setItem('user', JSON.stringify(user.value))
      
      return { success: true }
    } catch (error) {
      return { success: false, message: error.response?.data || '注册失败' }
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  function getToken() {
    return token.value
  }

  return {
    token,
    user,
    isAuthenticated,
    handleLogin,
    handleRegister,
    logout,
    getToken
  }
})
