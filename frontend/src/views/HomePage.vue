<template>
  <div class="wrap">
    <el-card class="filters">
      <div class="bar">
        <el-select v-model="query.category" placeholder="选择分类" clearable style="width:180px">
          <el-option v-for="c in categories" :key="c.categoryId||c.id" :label="c.name" :value="c.categoryId||c.id" />
        </el-select>
        <el-input v-model="query.keyword" placeholder="搜索商品" style="width:220px" />
        <el-select v-model="query.sort" placeholder="排序" style="width:160px">
          <el-option label="最新" value="newest" />
          <el-option label="价格升序" value="price_asc" />
          <el-option label="价格降序" value="price_desc" />
        </el-select>
        
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <div v-loading="loading" class="list">
      <template v-if="records.length">
        <el-row :gutter="16">
          <el-col :xs="12" :sm="8" :md="6" :lg="6" v-for="item in records" :key="item.productId">
            <el-card class="card" shadow="hover" @click="goDetail(item.productId)">
              <img class="img" :src="firstImage(item)" alt="" />
              <div class="info">
                <div class="title">{{ item.title }}</div>
                <div class="price">￥{{ formatPrice(item.price) }}</div>
                <el-tag size="small" class="tag">{{ statusText(item.status) }}</el-tag>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </template>
      <template v-else>
        <el-empty description="暂无相关商品" />
      </template>
    </div>

    <div class="pager" v-if="pages>1 || total>size">
      <el-pagination
        background
        layout="prev, pager, next, sizes, total"
        :total="total"
        :page-size="size"
        :current-page="page"
        :page-sizes="[10,20,30,50]"
        @current-change="onPageChange"
        @size-change="onSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, watch } from 'vue'
import { list as apiList } from '../api/products'
import { tree as apiTree } from '../api/categories'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const categories = ref([])
const records = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const pages = ref(1)
const query = reactive({ category: null, keyword: '', sort: 'newest', excludeActive: true })

function firstImage(item) { return Array.isArray(item.images) && item.images.length ? item.images[0] : 'https://via.placeholder.com/300x200' }
function formatPrice(p) { return Number(p).toFixed(2) }
function statusText() { return '在售' }

async function fetchList() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value, category: query.category, status: 1, keyword: query.keyword, sort: query.sort, excludeActive: true }
    const res = await apiList(params)
    const data = res.data || { records: [], total: 0, size: size.value, current: page.value, pages: 1 }
    records.value = data.records || []
    total.value = data.total || 0
    size.value = data.size || size.value
    page.value = data.current || page.value
    pages.value = data.pages || 1
  } catch (e) {
    records.value = []
    total.value = 0
    pages.value = 1
  } finally {
    loading.value = false
  }
}

async function fetchCategories() {
  try { const res = await apiTree(); categories.value = res.data || [] } catch { categories.value = [] }
}

function reset() { query.category=null; query.keyword=''; query.sort='newest'; query.excludeActive=true; page.value=1; size.value=10; fetchList() }
function onPageChange(p) { page.value = p; fetchList() }
function onSizeChange(s) { size.value = s>50?50:s<1?10:s; page.value = 1; fetchList() }
import { useUserStore } from '../stores/user'
const userStore = useUserStore()
function goDetail(id) {
  if (!userStore.userId) { router.push({ path: '/login', query: { redirect: `/products/${id}` } }) }
  else router.push(`/products/${id}`)
}

let kwTimer = null
watch(() => query.category, () => { page.value=1; fetchList() })
watch(() => query.sort, () => { page.value=1; fetchList() })
watch(() => query.keyword, () => { clearTimeout(kwTimer); kwTimer = setTimeout(() => { page.value=1; fetchList() }, 300) })
onMounted(() => { fetchCategories(); fetchList() })
</script>

<style scoped>
.wrap { padding: 16px; }
.filters { margin-bottom: 16px; }
.bar { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; }
.list { min-height: 200px; }
.card { cursor: pointer; }
.img { width: 100%; height: 160px; object-fit: cover; border-radius: 4px; }
.info { display: flex; align-items: center; justify-content: space-between; margin-top: 8px; }
.title { font-size: 14px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 65%; }
.price { color: #e74c3c; font-weight: 600; }
.tag { margin-left: 8px; }
.pager { margin-top: 16px; display: flex; justify-content: center; }
</style>
