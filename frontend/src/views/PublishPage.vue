<template>
  <div class="wrap">
    <el-card class="card">
      <div class="title">发布新商品</div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-cascader
            v-model="form.categoryId"
            :options="categories"
            :props="cascaderProps"
            clearable
            placeholder="请选择分类"
            style="width: 300px"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0.01" :max="maxPrice" :step="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请填写商品描述" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-form-item label="图片" prop="images">
          <el-upload
            action="#"
            list-type="picture-card"
            :file-list="fileList"
            :http-request="doUpload"
            :on-remove="onRemove"
            :limit="5"
            :disabled="uploading"
            accept="image/*"
          >
            <el-icon><i class="el-icon-plus"></i></el-icon>
          </el-upload>
          <div class="tip">最多上传 5 张，单张不超过 5MB</div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="onSubmit">发布</el-button>
          <el-button @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
  
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { create as apiCreate } from '../api/products'
import { tree as apiTree } from '../api/categories'
import { upload as apiUpload } from '../api/files'

const router = useRouter()

const form = reactive({ title: '', categoryId: null, price: 0.01, description: '', images: [] })
const maxPrice = 100000
const rules = {
  title: [
    { required: true, message: '请填写完整信息', trigger: 'blur' },
    { min: 2, max: 50, message: '长度为 2-50 字符', trigger: 'blur' },
  ],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [
    { required: true, message: '请填写价格', trigger: 'change' },
    { validator: (rule, value, callback) => { if (!value || Number(value) <= 0) callback(new Error('价格必须大于 0')); else if (Number(value) > maxPrice) callback(new Error('价格过大')); else callback() }, trigger: 'change' }
  ],
  description: [
    { required: true, message: '请填写描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '长度为 10-1000 字符', trigger: 'blur' },
  ],
  images: [{ validator: validateImages, trigger: 'change' }],
}
const formRef = ref()
const submitting = ref(false)
const uploading = ref(false)

const categories = ref([])
const cascaderProps = { value: 'categoryId', label: 'name', children: 'children', emitPath: false, checkStrictly: false }

const fileList = ref([])

function validateImages(rule, value, callback) {
  if (!Array.isArray(form.images) || form.images.length === 0) return callback(new Error('请至少上传 1 张图片'))
  callback()
}

function normalizeCategories(nodes) {
  if (!Array.isArray(nodes)) return []
  return nodes.map(n => ({
    ...n,
    categoryId: n.categoryId || n.id,
    children: normalizeCategories(n.children || []),
  }))
}

async function fetchCategories() {
  try {
    const res = await apiTree()
    categories.value = normalizeCategories(res.data || [])
  } catch {
    categories.value = []
  }
}

async function doUpload(options) {
  const { file, onError, onSuccess } = options
  if (!file) return onError && onError(new Error('无文件'))
  if (file.size > 5 * 1024 * 1024) return onError && onError(new Error('图片大小不能超过 5MB'))
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await apiUpload(fd)
    const url = (res?.data && (res.data.url || (Array.isArray(res.data.urls) ? res.data.urls[0] : null))) || (typeof res?.data === 'string' ? res.data : null)
    if (!url) throw new Error('上传失败')
    fileList.value.push({ name: file.name, url })
    form.images.push(url)
    onSuccess && onSuccess({ url })
  } catch (e) {
    ElMessage.error(e?.message || '上传失败')
    onError && onError(e)
  } finally {
    uploading.value = false
  }
}

function onRemove(file) {
  const url = file?.url
  fileList.value = fileList.value.filter(f => f.url !== url)
  form.images = form.images.filter(u => u !== url)
}

async function onSubmit() {
  let passed = true
  try {
    await formRef.value.validate()
  } catch (e) {
    passed = false
  }
  if (!passed) { ElMessage.warning('请完善表单后再提交'); return }
  submitting.value = true
  try {
    const payload = { title: form.title, description: form.description, price: form.price, categoryId: form.categoryId, images: form.images }
    await apiCreate(payload)
    ElMessage.success('发布成功')
    router.push('/')
  } catch (e) {
    ElMessage.error(e?.message || '请求失败')
  } finally {
    submitting.value = false
  }
}

function onReset() {
  form.title = ''
  form.categoryId = null
  form.price = 0.01
  form.description = ''
  form.images = []
  fileList.value = []
}

onMounted(fetchCategories)
</script>

<style scoped>
.wrap { display: flex; justify-content: center; padding: 24px 16px; }
.card { width: 800px; }
.title { font-size: 20px; font-weight: 600; margin-bottom: 16px; }
.tip { color: #999; font-size: 12px; margin-left: 12px; }
</style>
