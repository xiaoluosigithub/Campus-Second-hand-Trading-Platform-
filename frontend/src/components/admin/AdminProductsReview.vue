<template>
  <div>
    <el-row :gutter="16" v-loading="loading">
      <el-col :span="8" v-for="item in list" :key="item.productId">
        <el-card class="card">
          <img v-if="(item.images?.[0])" :src="item.images[0]" class="cover" />
          <div class="name">{{ item.title }}</div>
          <div class="price">￥{{ formatPrice(item.price) }}</div>
          <div class="desc">{{ item.description }}</div>
          <div class="ops">
            <el-button size="small" type="success" plain @click="approve(item)">通过</el-button>
            <el-button size="small" type="danger" plain @click="reject(item)">驳回</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-pagination background layout="prev, pager, next" :total="total" :page-size="size" :current-page="page" @current-change="onPage" />
    <el-dialog v-model="rejectVisible" title="填写驳回理由">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入驳回理由" />
      <template #footer>
        <el-button @click="rejectVisible=false">取消</el-button>
        <el-button type="danger" :loading="rejecting" @click="confirmReject">确定驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { reviewProduct } from '../../api/admin'
import http from '../../api/http'

const loading = ref(false)
const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

async function fetchData() {
  loading.value = true
  try {
    const params = { page: page.value, size: clampSize(size.value) }
    const res = await http.get('/products', { params: { ...params, status: 0 } })
    const data = res?.data || { records: [], total: 0 }
    list.value = data.records || []
    total.value = data.total || 0
    // pending count available in parent via separate query if needed
  } catch (e) { ElMessage.error(e?.message || '请求失败') } finally { loading.value = false }
}
function clampSize(n) { return Math.min(50, Math.max(1, Number(n||10))) }
function onPage(p) { page.value = p; fetchData() }
function formatPrice(p) { return Number(p||0).toFixed(2) }

const rejectVisible = ref(false)
const rejecting = ref(false)
const rejectItem = ref(null)
const rejectReason = ref('')

function approve(item) { doReview(item.productId, { approved: true }) }
function reject(item) { rejectItem.value = item; rejectReason.value = ''; rejectVisible.value = true }
async function confirmReject() {
  if (!rejectReason.value?.trim()) { ElMessage.warning('驳回理由不能为空'); return }
  rejecting.value = true
  try { await doReview(rejectItem.value.productId, { approved: false, reason: rejectReason.value }) ; rejectVisible.value = false } catch (e) { ElMessage.error(e?.message || '请求失败') } finally { rejecting.value = false }
}
async function doReview(id, payload) { await reviewProduct(id, payload); ElMessage.success('操作成功'); fetchData() }

onMounted(fetchData)
</script>

<style scoped>
.card { margin-bottom: 16px }
.cover { width: 100%; height: 120px; object-fit: cover; border-radius: 4px; }
.name { font-weight: 600; margin-top: 8px; }
.price { color: #409EFF; margin-top: 4px; }
.desc { color: #666; margin-top: 4px; min-height: 32px; }
.ops { margin-top: 8px; display: flex; gap: 8px; }
</style>
