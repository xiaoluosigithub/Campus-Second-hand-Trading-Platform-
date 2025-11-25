<template>
  <div class="admin">
    <aside class="sidebar">
      <div class="brand">Back Office</div>
      <el-menu :default-active="tab" class="menu" @select="onSelect">
        <el-menu-item index="review">商品审核</el-menu-item>
        <el-menu-item index="users">用户管理</el-menu-item>
        <el-menu-item index="categories">分类管理</el-menu-item>
        <el-menu-item index="orders">订单信息</el-menu-item>
        <el-menu-item index="stats">订单统计</el-menu-item>
      </el-menu>
      <div class="logout">
        <el-button type="danger" plain @click="logout">退出系统</el-button>
      </div>
    </aside>
    <main class="content">
      <div class="header">
        <div class="title">{{ titleMap[tab] }}</div>
        <el-tag v-if="pendingCount>0" type="warning">{{ pendingCount }} 待审核</el-tag>
      </div>
      <div v-show="tab==='review'">
        <AdminProductsReview @pending-change="pendingCount=$event" />
      </div>
      <div v-show="tab==='users'">
        <AdminUsers />
      </div>
      <div v-show="tab==='categories'">
        <AdminCategories />
      </div>
      <div v-show="tab==='orders'">
        <AdminOrders />
      </div>
      <div v-show="tab==='stats'">
        <AdminStats />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import AdminProductsReview from '../components/admin/AdminProductsReview.vue'
import AdminUsers from '../components/admin/AdminUsers.vue'
import AdminCategories from '../components/admin/AdminCategories.vue'
import AdminOrders from '../components/admin/AdminOrders.vue'
import AdminStats from '../components/admin/AdminStats.vue'

const tab = ref('review')
const pendingCount = ref(0)
const titleMap = { review: '商品审核', users: '用户管理', categories: '分类管理', orders: '订单信息', stats: '订单统计' }
const router = useRouter()
const store = useUserStore()

function onSelect(key) { tab.value = key }
async function logout() { await store.logout(); router.push('/') }
</script>

<style scoped>
.admin { display: flex; min-height: calc(100vh - 56px); }
.sidebar { width: 220px; border-right: 1px solid #eee; padding: 16px 12px; display: flex; flex-direction: column; }
.brand { font-weight: 600; margin-bottom: 8px; }
.menu { flex: 1; }
.logout { margin-top: 16px; }
.content { flex: 1; padding: 16px; }
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.title { font-size: 18px; font-weight: 600; }
</style>
