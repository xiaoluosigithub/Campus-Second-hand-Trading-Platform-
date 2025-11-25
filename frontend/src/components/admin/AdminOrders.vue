<template>
  <div>
    <div class="filters">
      <el-input v-model.number="buyerId" placeholder="买家ID" style="width:140px" />
      <el-input v-model.number="sellerId" placeholder="卖家ID" style="width:140px" />
      <el-select v-model="status" placeholder="状态" style="width:140px" clearable>
        <el-option :value="0" label="待发货" />
        <el-option :value="1" label="待收货" />
        <el-option :value="2" label="已完成" />
        <el-option :value="3" label="已取消" />
      </el-select>
      <el-date-picker v-model="date" type="date" placeholder="日期" />
      <el-button type="primary" @click="fetch">查询</el-button>
    </div>
    <el-table :data="list" v-loading="loading" style="width:100%">
      <el-table-column prop="orderNumber" label="订单号" width="160" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column prop="transactionPrice" label="成交价" width="100" />
      <el-table-column prop="buyerId" label="买家" width="100" />
      <el-table-column prop="sellerId" label="卖家" width="100" />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{row}"><el-button size="small" @click="openDetail(row)">详情</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="prev, pager, next" :total="total" :page-size="size" :current-page="page" @current-change="onPage" />

    <el-drawer v-model="detailVisible" title="订单详情" size="40%">
      <div v-if="detail" class="drawer">
        <div class="header">
          <div class="left">
            <div class="ord">订单号：{{ field(detail,'orderNumber','order_number') }}</div>
            <el-button text @click="copy(field(detail,'orderNumber','order_number'))">复制</el-button>
          </div>
          <div class="right">
            <el-tag :type="statusTagType(detail.status)">{{ statusText(detail.status) }}</el-tag>
            <div class="amount">￥{{ formatPrice(field(detail,'transactionPrice','transaction_price')) }}</div>
          </div>
        </div>
        <el-divider />

        <el-descriptions title="基本信息" :column="3" border>
          <el-descriptions-item label="买家ID">{{ field(detail,'buyerId','buyer_id') }}</el-descriptions-item>
          <el-descriptions-item label="卖家ID">{{ field(detail,'sellerId','seller_id') }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(field(detail,'createdAt','created_at')) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatTime(field(detail,'updatedAt','updated_at')) }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="收货信息" :column="2" border class="mt">
          <el-descriptions-item label="收件人">{{ field(detail,'shippingName','shipping_name') }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ field(detail,'shippingPhone','shipping_phone') }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">
            <div class="addr">
              <span>{{ field(detail,'shippingAddress','shipping_address') }}</span>
              <el-button text @click="copy(field(detail,'shippingAddress','shipping_address'))">复制</el-button>
            </div>
          </el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="物流信息" :column="2" border class="mt">
          <el-descriptions-item label="物流单号">{{ field(detail,'trackingNumber','tracking_number') || '-' }}</el-descriptions-item>
        </el-descriptions>

        
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ordersList, orderDetail } from '../../api/admin'

const loading = ref(false)
const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const buyerId = ref()
const sellerId = ref()
const status = ref()
const date = ref()

function clampSize(n) { return Math.min(50, Math.max(1, Number(n||10))) }
function fmtDate(d) { return d ? new Date(d).toISOString().slice(0,10) : undefined }
async function fetch() { loading.value=true; try { const res = await ordersList({ page: page.value, size: clampSize(size.value), buyerId: buyerId.value||undefined, sellerId: sellerId.value||undefined, status: status.value||undefined, date: fmtDate(date.value)||undefined }); const data=res?.data||{ records:[], total:0 }; list.value=data.records||[]; total.value=data.total||0 } catch(e){ ElMessage.error(e?.message||'请求失败') } finally { loading.value=false } }
function onPage(p){ page.value=p; fetch() }
function formatPrice(p){ return Number(p||0).toFixed(2) }
function formatTime(t){ return t ? String(t).replace('T',' ').slice(0,19) : '' }
function statusText(s){ return s===0?'待发货':s===1?'待收货':s===2?'已完成':'已取消' }
function statusTagType(s){ return s===0?'warning':s===1?'info':s===2?'success':'danger' }
async function copy(text){ try{ await navigator.clipboard.writeText(String(text||'')); ElMessage.success('已复制') } catch(e){ ElMessage.warning('复制失败') } }

function field(obj, ...keys){ for(const k of keys){ const v = obj?.[k]; if(v!==undefined && v!==null && v!=='') return v } return undefined }
 

const detailVisible = ref(false)
const detail = ref(null)
async function openDetail(row){ try{ const res = await orderDetail(row.orderId); detail.value=res?.data||null; detailVisible.value=true } catch(e){ ElMessage.error(e?.message||'请求失败') } }

onMounted(fetch)
</script>

<style scoped>
.filters { display:flex; gap:8px; margin-bottom:12px; align-items:center }
.drawer { display:flex; flex-direction:column }
.header { display:flex; justify-content:space-between; align-items:center }
.header .left { display:flex; align-items:center; gap:8px }
.amount { font-weight:700; color:#409EFF; margin-left:8px }
.mt { margin-top:12px }

</style>
