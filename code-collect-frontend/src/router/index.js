import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { guest: true }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/snippets/new',
    name: 'NewSnippet',
    component: () => import('../views/SnippetForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/snippets/:id/edit',
    name: 'EditSnippet',
    component: () => import('../views/SnippetForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/snippets/:id',
    name: 'SnippetDetail',
    component: () => import('../views/SnippetDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/categories',
    name: 'Categories',
    component: () => import('../views/Categories.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/starred',
    name: 'Starred',
    component: () => import('../views/Starred.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.guest && authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router
