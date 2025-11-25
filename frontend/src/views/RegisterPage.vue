<template>
  <div class="wrap">
    <el-card class="card">
      <div class="title">注册</div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">注册</el-button>
          <el-button type="text" @click="goLogin">已有账户？立即登录</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register as apiRegister } from '../api/auth'

const router = useRouter()
const form = reactive({ username: '', password: '', confirmPassword: '', nickname: '' })
const rules = {
  username: [{ required: true, message: '请填写完整信息', trigger: 'blur' }],
  password: [{ required: true, message: '请填写完整信息', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请填写完整信息', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }],
  nickname: [{ required: true, message: '请填写完整信息', trigger: 'blur' }],
}
function validateConfirm(rule, value, callback) {
  if (value !== form.password) callback(new Error('两次输入密码不一致'))
  else callback()
}
const formRef = ref()
const loading = ref(false)

function goLogin() { router.push('/login') }
function goBack() { router.back() }

async function onSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await apiRegister({ username: form.username, password: form.password, confirmPassword: form.confirmPassword, nickname: form.nickname })
    ElMessage.success('注册成功')
    router.replace('/login')
  } catch (e) {
    ElMessage.error(`注册失败：${e?.message || '请求失败'}`)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.wrap { display: flex; justify-content: center; padding: 40px 16px; }
.card { width: 480px; }
.title { font-size: 20px; font-weight: 600; margin-bottom: 16px; }
</style>
