<template>
  <div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索用户" style="width:260px" clearable @change="fetch" />
    </div>
    <el-table :data="list" v-loading="loading" style="width:100%">
      <el-table-column label="头像" width="80">
        <template #default="{row}"><el-avatar :size="28" :src="row.avatarUrl" /></template>
      </el-table-column>
      <el-table-column prop="username" label="账号" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="role" label="角色" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="220">
        <template #default="{row}">
          <el-button size="small" type="warning" plain @click="toggleRole(row)">{{ row.role==='admin'?'设为用户':'设为管理员' }}</el-button>
          <el-button size="small" type="danger" plain @click="removeUser(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="prev, pager, next" :total="total" :page-size="size" :current-page="page" @current-change="onPage" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { usersList, userPatchRole, userDelete } from '../../api/admin'

const loading = ref(false)
const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')

function clampSize(n) { return Math.min(50, Math.max(1, Number(n||10))) }
async function fetch() {
  loading.value = true
  try { const res = await usersList({ page: page.value, size: clampSize(size.value), keyword: keyword.value || undefined }); const data=res?.data||{ records:[], total:0 }; list.value=data.records||[]; total.value=data.total||0 } catch (e) { ElMessage.error(e?.message||'请求失败') } finally { loading.value=false }
}
function onPage(p) { page.value=p; fetch() }
async function toggleRole(row) { try { await userPatchRole(row.userId, row.role==='admin'?'user':'admin'); ElMessage.success('角色已更新'); fetch() } catch (e) { ElMessage.error(e?.message||'请求失败') } }
async function removeUser(row) { try { await ElMessageBox.confirm('确认删除该用户？','提示'); await userDelete(row.userId); ElMessage.success('删除成功'); fetch() } catch (e) { if (e!=='cancel') ElMessage.error(e?.message||'请求失败') } }

onMounted(fetch)
</script>

<style scoped>
.toolbar { margin-bottom: 12px; display:flex; justify-content:flex-end }
</style>
