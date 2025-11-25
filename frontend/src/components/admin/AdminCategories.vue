<template>
  <div class="wrap">
    <div class="left">
      <el-table :data="list" v-loading="loading" style="width:100%">
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作" width="120">
          <template #default="{row}">
            <el-button size="small" type="danger" plain @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="size" :current-page="page" @current-change="onPage" />
    </div>
    <div class="right">
      <el-card>
        <div class="title">新增分类</div>
        <el-form :model="form">
          <el-form-item label="名称">
            <el-input v-model="form.name" placeholder="请输入名称" />
          </el-form-item>
          <el-button type="primary" @click="create">添加分类</el-button>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoriesList, categoriesCreate, categoriesDelete } from '../../api/admin'

const loading = ref(false)
const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const form = ref({ name: '' })

function clampSize(n) { return Math.min(50, Math.max(1, Number(n||10))) }
async function fetch() { loading.value=true; try { const res = await categoriesList({ page: page.value, size: clampSize(size.value) }); const data=res?.data||{ records:[], total:0 }; list.value=data.records||[]; total.value=data.total||0 } catch(e){ ElMessage.error(e?.message||'请求失败') } finally { loading.value=false } }
function onPage(p){ page.value=p; fetch() }
async function create(){ if(!form.value.name?.trim()){ ElMessage.warning('名称不能为空'); return } try{ await categoriesCreate({ name: form.value.name }); ElMessage.success('创建成功'); form.value.name=''; fetch() } catch(e){ ElMessage.error(e?.message||'请求失败') } }
async function remove(row){ try{ await ElMessageBox.confirm('确认删除该分类？','提示'); await categoriesDelete(row.categoryId); ElMessage.success('删除成功'); fetch() } catch(e){ if(e!=='cancel') ElMessage.error(e?.message||'请求失败') } }

onMounted(fetch)
</script>

<style scoped>
.wrap { display:flex; gap:16px }
.left { flex:1 }
.right { width:360px }
.title { font-weight:600; margin-bottom:8px }
</style>
