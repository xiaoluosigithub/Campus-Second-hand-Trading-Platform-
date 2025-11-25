<template>
  <div class="wrap">
    <el-card class="card" v-loading="loading">
      <div class="title">订单确认</div>
      <el-descriptions border :column="1" v-if="product">
        <el-descriptions-item label="商品">{{ product.title }}</el-descriptions-item>
        <el-descriptions-item label="价格">￥{{ formatPrice(product.price) }}</el-descriptions-item>
      </el-descriptions>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="form">
        <el-form-item label="收件人" prop="shippingName">
          <el-input v-model="form.shippingName" />
        </el-form-item>
        <el-form-item label="电话" prop="shippingPhone">
          <el-input v-model="form.shippingPhone" />
        </el-form-item>
        <el-form-item label="地址" prop="shippingAddress">
          <el-input v-model="form.shippingAddress" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-radio-group v-model="form.paymentMethod">
            <el-radio label="alipay">支付宝(模拟)</el-radio>
            <el-radio label="wechat">微信支付(模拟)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit">去支付</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { detail as apiDetail } from '../api/products'
import { create as apiCreateOrder } from '../api/orders'
import { mock as apiPayMock } from '../api/pay'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { precheck as apiPrecheck } from '../api/orders'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const product = ref(null)
const formRef = ref()
const form = ref({ shippingName: '', shippingPhone: '', shippingAddress: '', paymentMethod: 'alipay' })
const rules = {
  shippingName: [{ required: true, message: '请填写完整信息', trigger: 'blur' }],
  shippingPhone: [
    { required: true, message: '请填写完整信息', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '请填写有效手机号（1XXXXXXXXXX）', trigger: 'blur' }
  ],
  shippingAddress: [{ required: true, message: '请填写完整信息', trigger: 'blur' }],
  paymentMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }],
}

function formatPrice(p) { return Number(p || 0).toFixed(2) }
function goBack() { router.back() }

async function load() {
  const productId = Number(route.query.productId)
  if (!productId) { ElMessage.error('参数错误'); return }
  loading.value = true
  try {
    const res = await apiDetail(productId)
    product.value = res?.data?.product || null
    if (product.value && userStore.userId && product.value.sellerId === userStore.userId) { ElMessage.warning('不能购买自己的商品'); router.push(`/products/${productId}`); return }
  } catch (e) {
    ElMessage.error(e?.message || '请求失败')
  } finally {
    loading.value = false
  }
}

async function submit() {
  let ok = true
  try { await formRef.value.validate() } catch { ok = false }
  if (!ok) { ElMessage.warning('请完善表单后再提交'); return }
  const pm = form.value.paymentMethod
  if (pm !== 'alipay' && pm !== 'wechat') { ElMessage.error('支付方式仅支持支付宝或微信'); return }
  try {
    const pre = await apiPrecheck({ productId: product.value.productId })
    if (!pre?.data) { ElMessage.error('商品已失效或非在售'); return }
  } catch (e) { ElMessage.error(e?.message || '请求失败'); return }
  submitting.value = true
  try {
    const payload = { productId: product.value.productId, shippingName: form.value.shippingName, shippingPhone: form.value.shippingPhone, shippingAddress: form.value.shippingAddress, paymentMethod: form.value.paymentMethod }
    const res = await apiCreateOrder(payload)
    const orderId = res?.data
    ElMessage.success('订单创建成功')
    if (orderId) {
      try { await apiPayMock(orderId, form.value.paymentMethod); ElMessage.success('支付成功') } catch (err) { /* ignore */ }
    }
    router.push('/me')
  } catch (e) {
    ElMessage.error(e?.message || '请求失败')
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.wrap { padding: 16px; display: flex; justify-content: center; }
.card { width: 720px; }
.title { font-size: 20px; font-weight: 600; margin-bottom: 12px; }
.form { margin-top: 12px; }
</style>
