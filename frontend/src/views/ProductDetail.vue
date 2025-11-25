<template>
  <div class="wrap" v-loading="loading">
    <Breadcrumbs :categoryId="product?.categoryId || 0" />
    <el-row :gutter="16">
      <el-col :xs="24" :md="12">
        <el-card>
          <el-carousel height="300px">
            <el-carousel-item v-for="(img,i) in images" :key="i">
              <img class="img" :src="img" alt="" />
            </el-carousel-item>
          </el-carousel>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card>
          <div class="title">{{ product?.title }}</div>
          <div class="price">￥{{ formatPrice(product?.price) }}</div>
          <el-tag class="tag" type="success" v-if="product?.status===1">在售</el-tag>
          <el-tag class="tag" type="info" v-else-if="product?.status===0">待审核</el-tag>
          <el-tag class="tag" type="danger" v-else-if="product?.status===2">审核未通过</el-tag>
          <el-tag class="tag" type="warning" v-else-if="product?.status===3">已售出</el-tag>
          <el-tag class="tag" type="default" v-else>已下架</el-tag>
          <div class="desc">{{ product?.description }}</div>
          <div class="time">发布于 {{ formatTime(product?.createdAt) }}</div>
          <div class="views">浏览量：{{ product?.viewCount ?? 0 }}</div>
          <div class="ops">
            <el-button type="primary" :disabled="!isOnSale" @click="addToCart">加入购物车</el-button>
            <el-button type="danger" :disabled="!isOnSale" @click="buyNow">立即购买</el-button>
            <el-button @click="chat">聊一聊</el-button>
            <el-button @click="back">返回</el-button>
          </div>
        </el-card>
        <el-card class="seller">
          <div class="seller-box">
            <el-avatar :size="40" :src="seller.avatarUrl" />
            <div class="seller-name">{{ seller.nickname }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Breadcrumbs from '../components/Breadcrumbs.vue'
import { detail as apiDetail } from '../api/products'
import { add as apiAddCart } from '../api/cart'
import { precheck as apiPrecheck } from '../api/orders'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const product = ref(null)
const images = ref([])
const seller = ref({ avatarUrl: '', nickname: '' })
const isOnSale = ref(false)
const isSelfSeller = ref(false)

function formatPrice(p) { return p ? Number(p).toFixed(2) : '0.00' }
function formatTime(t) { if (!t) return ''; const d = new Date(t); return d.toLocaleString() }

async function load() {
  loading.value = true
  try {
    const id = route.params.id
    const res = await apiDetail(id)
    const data = res.data || {}
    product.value = data.product || null
    images.value = Array.isArray(product.value?.images) ? product.value.images : []
    seller.value = { avatarUrl: data.sellerAvatarUrl || '', nickname: data.sellerNickname || '' }
    isOnSale.value = product.value?.status === 1
    isSelfSeller.value = !!userStore.userId && product.value?.sellerId === userStore.userId
  } catch (e) {
    ElMessage.error(e?.message || '请求失败')
  } finally {
    loading.value = false
  }
}

async function addToCart() {
  if (isSelfSeller.value) { ElMessage.warning('不能将自己的商品加入购物车'); return }
  try { await apiAddCart({ productId: product.value.productId }); ElMessage.success('已加入购物车') } catch (e) { ElMessage.error(e?.message || '请求失败') }
}

async function buyNow() {
  if (isSelfSeller.value) { ElMessage.warning('不能购买自己的商品'); return }
  try {
    const res = await apiPrecheck({ productId: product.value.productId })
    if (res?.data) { ElMessage.success('预校验通过'); router.push({ path: '/orders/confirm', query: { productId: String(product.value.productId) } }) }
    else { ElMessage.error('商品已失效或非在售') }
  } catch (e) { ElMessage.error(e?.message || '请求失败') }
}

function chat() {
  if (isSelfSeller.value) { ElMessage.warning('与自己无法聊天'); return }
  const sid = product.value?.sellerId
  const sname = seller.value?.nickname || ''
  router.push({ path: '/me', query: { tab: 'messages', contactId: sid ? String(sid) : '', contactName: sname } })
}
function back() { router.back() }

onMounted(load)
</script>

<style scoped>
.wrap { padding: 16px; }
.img { width: 100%; height: 300px; object-fit: cover; }
.title { font-size: 20px; font-weight: 600; margin-bottom: 8px; }
.price { color: #e74c3c; font-size: 18px; font-weight: 700; }
.tag { margin-left: 8px; }
.desc { margin-top: 12px; line-height: 1.6; }
.time { margin-top: 8px; color: #666; }
.views { margin-top: 4px; color: #666; }
.ops { margin-top: 16px; display: flex; gap: 12px; }
.seller { margin-top: 16px; }
.seller-box { display: flex; align-items: center; gap: 12px; }
.seller-name { font-weight: 600; }
</style>

<style scoped>
</style>
