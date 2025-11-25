<template>
  <div class="wrap">
    <el-card class="card">
      <div class="title">登录</div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">登录</el-button>
          <el-button type="text" @click="goRegister">还没有账户？立即注册</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const store = useUserStore()

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请填写完整信息', trigger: 'blur' }],
  password: [{ required: true, message: '请填写完整信息', trigger: 'blur' }],
}
const formRef = ref()
const loading = ref(false)

function goRegister() { router.push('/register') }
function goBack() { router.back() }

async function onSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await store.login({ username: form.username, password: form.password })
    await store.getMe()
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || (store.role === 'admin' ? '/admin' : '/')
    const target = String(redirect)
    if (target.startsWith('/admin') && store.role !== 'admin') {
      router.replace('/')
    } else {
      router.replace(target)
    }
  } catch (e) {
    ElMessage.error(`登录失败：${e?.message || '请求失败'}`)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.wrap { display: flex; justify-content: center; padding: 40px 16px; }
.card { width: 400px; }
.title { font-size: 20px; font-weight: 600; margin-bottom: 16px; }
</style>
