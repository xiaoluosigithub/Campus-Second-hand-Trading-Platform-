import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/HomePage.vue') },
  { path: '/login', name: 'login', component: () => import('../views/LoginPage.vue') },
  { path: '/register', name: 'register', component: () => import('../views/RegisterPage.vue') },
  { path: '/products/:id', name: 'product-detail', component: () => import('../views/ProductDetail.vue') },
  { path: '/publish', name: 'publish', component: () => import('../views/PublishPage.vue'), meta: { requiresAuth: true } },
  { path: '/orders/confirm', name: 'order-confirm', component: () => import('../views/OrderConfirm.vue'), meta: { requiresAuth: true } },
  { path: '/me', name: 'me', component: () => import('../views/PersonalCenter.vue'), meta: { requiresAuth: true } },
  { path: '/admin', name: 'admin', component: () => import('../views/AdminPanel.vue'), meta: { requiresAdmin: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  const store = useUserStore()
  if (!store.initialized) {
    await store.getMe()
  }
  if (to.meta?.requiresAdmin) {
    if (!store.userId) return { path: '/login', query: { redirect: to.fullPath } }
    if (store.role !== 'admin') return { path: '/' }
  }
  if (to.meta?.requiresAuth) {
    if (!store.userId) return { path: '/login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
