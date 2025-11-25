<template>
  <div>
    <div class="filters">
      <el-date-picker v-model="day" type="date" placeholder="选择日期" />
      <el-button type="primary" @click="fetchDay">查询日统计</el-button>
      <el-date-picker v-model="range" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" />
      <el-button @click="fetchRange">查询区间统计</el-button>
    </div>
    <el-card v-if="stat" class="card">
      <div class="grid">
        <div>订单数：{{ stat.total }}</div>
        <div>销售额：￥{{ formatPrice(stat.amount) }}</div>
        <div>已完成金额：￥{{ formatPrice(stat.amountCompleted) }}</div>
      </div>
      <div class="bars">
        <div class="bar"><span>待发货</span><em>{{ stat.status?.pending||0 }}</em></div>
        <div class="bar"><span>待收货</span><em>{{ stat.status?.shipped||0 }}</em></div>
        <div class="bar"><span>已完成</span><em>{{ stat.status?.completed||0 }}</em></div>
        <div class="bar"><span>已取消</span><em>{{ stat.status?.canceled||0 }}</em></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { statsDay, statsRange } from '../../api/admin'

const day = ref()
const range = ref()
const stat = ref(null)

function fmtDate(d){ return d ? new Date(d).toISOString().slice(0,10) : undefined }
function formatPrice(p){ return Number(p||0).toFixed(2) }

async function fetchDay(){ if(!day.value){ ElMessage.warning('请选择日期'); return } try{ const res = await statsDay({ date: fmtDate(day.value) }); stat.value = res?.data || null } catch(e){ ElMessage.error(e?.message||'请求失败') } }
async function fetchRange(){ if(!range.value || range.value.length!==2){ ElMessage.warning('请选择区间'); return } try{ const res = await statsRange({ start: fmtDate(range.value[0]), end: fmtDate(range.value[1]) }); stat.value = res?.data || null } catch(e){ ElMessage.error(e?.message||'请求失败') } }
</script>

<style scoped>
.filters { display:flex; gap:8px; margin-bottom:12px; align-items:center }
.card { padding: 12px }
.grid { display:grid; grid-template-columns: repeat(3, 1fr); gap:8px; margin-bottom:12px }
.bars { display:grid; grid-template-columns: repeat(4, 1fr); gap:8px }
.bar { background:#f5f7fa; border-radius:8px; padding:10px; display:flex; justify-content:space-between }
.bar em { font-style:normal; font-weight:600 }
</style>
