<template>
  <div class="nav">
    <div class="left" @click="goHome">二手交易</div>
    <div class="right">
      <template v-if="!userStore.userId">
        <el-button type="primary" @click="goLogin">登录</el-button>
        <el-button @click="goRegister">注册</el-button>
      </template>
      <template v-else>
        <el-badge :value="unread" :hidden="unread===0" class="badge">
          <el-button @click="goMessages">消息</el-button>
        </el-badge>
        <el-button v-if="userStore.role==='admin'" type="warning" @click="goAdmin">后台管理</el-button>
        <el-button type="success" @click="goPublish">发布新商品</el-button>
        <el-avatar :size="30" :src="userStore.avatarUrl" @click="goProfile" style="cursor:pointer" />
        <span class="nick">{{ userStore.nickname }}</span>
        <el-button @click="logout">退出</el-button>
      </template>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { unreadCount } from '../api/messages'

const router = useRouter()
const userStore = useUserStore()
const unread = ref(0)

function goHome() { router.push('/') }
function goLogin() { router.push({ path: '/login' }) }
function goRegister() { router.push({ path: '/register' }) }
function goPublish() { router.push({ path: '/publish' }) }
function goMessages() { router.push({ path: '/me', query: { tab: 'messages' } }) }
function goAdmin() { router.push('/admin') }
function goProfile() { router.push({ path: '/me' }) }
async function refreshUnread() {
  if (!userStore.userId) { unread.value = 0; return }
  try { const res = await unreadCount(); unread.value = res.data || 0 } catch { unread.value = 0 }
}
async function logout() { await userStore.logout(); router.push('/') }

onMounted(refreshUnread)
watch(() => userStore.userId, refreshUnread)
</script>

<style scoped>
.nav { height: 56px; display: flex; align-items: center; justify-content: space-between; padding: 0 16px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); background: #fff; position: sticky; top: 0; z-index: 100; }
.left { font-weight: 600; cursor: pointer; }
.right { display: flex; align-items: center; gap: 12px; }
.nick { margin-left: 8px; }
.badge { margin-right: 8px; }
</style>
