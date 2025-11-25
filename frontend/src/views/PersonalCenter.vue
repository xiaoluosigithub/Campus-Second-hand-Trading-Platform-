<template>
  <div class="wrap">
    <el-card class="card">
      <div class="head">
        <el-avatar :size="48" :src="profile.avatarUrl || userStore.avatarUrl" />
        <div class="meta">
          <div class="nick">{{ userStore.nickname }}</div>
          <div class="uid">ID: {{ userStore.userId }}</div>
        </div>
      </div>
      <el-tabs v-model="active">
        <el-tab-pane label="资料" name="profile" lazy>
          <el-form :model="profile" :rules="profileRules" ref="profileRef" label-width="90px">
            <el-form-item label="头像" prop="avatarUrl">
              <el-upload
                ref="uploadAvatarRef"
                action="#"
                list-type="picture-card"
                :file-list="avatarList"
                :http-request="doUploadAvatar"
                :on-remove="onRemoveAvatar"
                :limit="1"
                :on-exceed="onAvatarExceed"
                accept="image/*"
              >
                <el-icon><i class="el-icon-plus"></i></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profile.nickname" maxlength="50" show-word-limit />
            </el-form-item>
            <el-form-item label="个性签名" prop="signature">
              <el-input v-model="profile.signature" type="textarea" :rows="3" maxlength="200" show-word-limit />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingProfile" @click="saveProfile">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="我的商品" name="products" lazy>
          <div class="bar">
            <el-select v-model="prodQuery.status" placeholder="状态" style="width:160px" clearable>
              <el-option label="待审核" :value="0" />
              <el-option label="在售" :value="1" />
              <el-option label="审核未通过" :value="2" />
              <el-option label="已售出" :value="3" />
              <el-option label="已下架" :value="4" />
            </el-select>
            <el-input v-model="prodQuery.keyword" placeholder="搜索标题" style="width:220px" />
            <el-select v-model="prodQuery.sort" placeholder="排序" style="width:160px">
              <el-option label="最新" value="newest" />
              <el-option label="价格升序" value="price_asc" />
              <el-option label="价格降序" value="price_desc" />
            </el-select>
            <el-button @click="resetMine">重置</el-button>
          </div>
          <div v-loading="mineLoading" class="list">
            <template v-if="mineRecords.length">
              <el-row :gutter="16">
                <el-col :xs="12" :sm="8" :md="6" :lg="6" v-for="item in mineRecords" :key="item.productId">
                  <el-card class="card2">
                    <img class="img" :src="firstImage(item)" />
                    <div class="info">
                      <div class="title">{{ item.title }}</div>
                      <div class="price">￥{{ formatPrice(item.price) }}</div>
                    </div>
                    <div class="ops">
                      <el-tag size="small">{{ statusText(item.status) }}</el-tag>
                      <el-tag v-if="item.status===2 && item.rejectionReason" type="danger" size="small">{{ item.rejectionReason }}</el-tag>
                      <el-button size="small" v-if="canEdit(item)" @click="openEdit(item)">编辑</el-button>
                      <el-button size="small" v-if="canDown(item)" @click="down(item)">下架</el-button>
                      <el-button size="small" v-if="canUp(item)" @click="up(item)">上架</el-button>
                      <el-button size="small" type="danger" v-if="canDelete(item)" @click="del(item)">删除</el-button>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
            </template>
            <template v-else>
              <el-empty description="暂无商品" />
            </template>
          </div>
          <div class="pager" v-if="minePages>1 || mineTotal>mineSize">
            <el-pagination
              background
              layout="prev, pager, next, sizes, total"
              :total="mineTotal"
              :page-size="mineSize"
              :current-page="minePage"
              :page-sizes="[10,20,30,50]"
              @current-change="onMinePage"
              @size-change="onMineSize"
            />
          </div>
          <el-dialog v-model="editVisible" title="编辑商品" width="480px">
            <el-form :model="editForm" label-width="90px">
              <el-form-item label="价格">
                <el-input-number v-model="editForm.price" :min="0.01" :step="0.01" :precision="2" />
              </el-form-item>
              <el-form-item label="描述">
                <el-input v-model="editForm.description" type="textarea" :rows="4" maxlength="1000" show-word-limit />
              </el-form-item>
            </el-form>
            <template #footer>
              <el-button @click="editVisible=false">取消</el-button>
              <el-button type="primary" :loading="editing" @click="submitEdit">保存</el-button>
            </template>
          </el-dialog>
        </el-tab-pane>
        <el-tab-pane label="购物车" name="cart" lazy>
          <div class="bar">
            <el-button type="warning" @click="cleanupCart">一键清除失效商品</el-button>
          </div>
          <el-table :data="cartRecords" v-loading="cartLoading" style="width:100%" @selection-change="onCartSelectionChange">
            <el-table-column type="selection" :selectable="rowSelectable" width="48" />
            <el-table-column prop="title" label="商品" />
            <el-table-column prop="price" label="价格" :formatter="colPrice" />
            <el-table-column prop="status" label="状态" :formatter="colStatus" />
            <el-table-column prop="addedAt" label="加入时间" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" @click="goDetail(scope.row.productId)">查看</el-button>
                <el-button size="small" type="primary" :disabled="scope.row.status!==1" @click="checkoutRow(scope.row)">去结算</el-button>
                <el-button size="small" type="danger" @click="removeCart(scope.row.productId)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="summary">
            <div>已选 {{ selectedCart.length }} 件，合计：￥{{ formatPrice(cartSelectedAmount) }}</div>
            <el-button type="primary" @click="checkoutSelected" :disabled="selectedCart.length!==1">去结算（选中）</el-button>
          </div>
          <div class="pager" v-if="cartPages>1 || cartTotal>cartSize">
            <el-pagination
              background
              layout="prev, pager, next, sizes, total"
              :total="cartTotal"
              :page-size="cartSize"
              :current-page="cartPage"
              :page-sizes="[10,20,30,50]"
              @current-change="onCartPage"
              @size-change="onCartSize"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="我买到的" name="bought" lazy>
          <el-table :data="boughtRecords" v-loading="boughtLoading" style="width:100%">
            <el-table-column prop="orderNumber" label="订单号" />
            <el-table-column prop="productTitleSnapshot" label="商品" />
            <el-table-column prop="transactionPrice" label="价格" :formatter="colPrice" />
            <el-table-column prop="status" label="状态" :formatter="colOrderStatus" />
            <el-table-column prop="createdAt" label="创建时间" />
            <el-table-column label="操作" width="240">
              <template #default="scope">
                <el-button size="small" type="primary" v-if="scope.row.status===1" @click="confirmOrder(scope.row.orderId)">确认收货</el-button>
                <el-button size="small" type="danger" v-if="scope.row.status===0" @click="cancelOrder(scope.row.orderId)">取消订单</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pager" v-if="boughtPages>1 || boughtTotal>boughtSize">
            <el-pagination
              background
              layout="prev, pager, next, sizes, total"
              :total="boughtTotal"
              :page-size="boughtSize"
              :current-page="boughtPage"
              :page-sizes="[10,20,30,50]"
              @current-change="onBoughtPage"
              @size-change="onBoughtSize"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="我卖出的" name="sold" lazy>
          <div class="bar">
            <el-dialog v-model="shipVisible" title="填写物流单号" width="420px">
              <el-input v-model="trackingNumber" placeholder="物流单号" />
              <template #footer>
                <el-button @click="shipVisible=false">取消</el-button>
                <el-button type="primary" :loading="shipping" @click="submitShip">确认发货</el-button>
              </template>
            </el-dialog>
          </div>
          <el-table :data="soldRecords" v-loading="soldLoading" style="width:100%">
            <el-table-column prop="orderNumber" label="订单号" />
            <el-table-column prop="productTitleSnapshot" label="商品" />
            <el-table-column prop="transactionPrice" label="价格" :formatter="colPrice" />
            <el-table-column prop="status" label="状态" :formatter="colOrderStatus" />
            <el-table-column prop="createdAt" label="创建时间" />
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button size="small" type="primary" v-if="scope.row.status===0" @click="openShip(scope.row)">去发货</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pager" v-if="soldPages>1 || soldTotal>soldSize">
            <el-pagination
              background
              layout="prev, pager, next, sizes, total"
              :total="soldTotal"
              :page-size="soldSize"
              :current-page="soldPage"
              :page-sizes="[10,20,30,50]"
              @current-change="onSoldPage"
              @size-change="onSoldSize"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="我的消息" name="messages" lazy>
          <div class="msg">
            <div class="left">
              <div class="bar">
                <el-button @click="refreshContacts">刷新联系人</el-button>
              </div>
              <el-scrollbar class="list3">
                <div class="item3" v-for="c in contactsList" :key="c.contactId" :class="{active: c.contactId===contactId}" @click="selectContact(c.contactId)">
                  <div class="row3">
                    <el-avatar :size="26" :src="c.contactAvatarUrl || defaultAvatar" />
                    <div class="name">{{ contactDisplayName(c) }}</div>
                  </div>
                  <div class="time">{{ c.lastAt }}</div>
                </div>
              </el-scrollbar>
            </div>
            <div class="right">
              <div class="bar">
                <el-button @click="refreshMessages">刷新消息</el-button>
              </div>
              <el-scrollbar class="list4" ref="messagesSbRef">
                <div class="item4" v-for="m in messagesList" :key="m.messageId" :class="{ mine: m.senderId===userStore.userId, other: m.senderId!==userStore.userId }">
                  <el-avatar :size="26" class="avatar" :src="m.senderId===userStore.userId ? (userStore.avatarUrl || defaultAvatar) : (m.senderAvatarUrl || defaultAvatar)" />
                  <div class="bubble">
                    <div class="text">{{ m.content }}</div>
                    <div class="time">{{ m.createdAt }}</div>
                  </div>
                </div>
              </el-scrollbar>
              <div class="send">
                <el-input v-model="sendContent" placeholder="输入消息" />
                <el-button type="primary" :disabled="!contactId || !sendContent" @click="sendMessage">发送</el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed, watch, nextTick } from 'vue'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMe, updateProfile } from '../api/users'
import { upload as uploadFile } from '../api/files'
import { mine as productsMine, patchStatus as productPatchStatus, update as productUpdate, remove as productRemove } from '../api/products'
import { items as cartItems, cleanup as cartCleanup, remove as cartRemove } from '../api/cart'
import { bought as ordersBought, sold as ordersSold, confirm as orderConfirm, ship as orderShip, precheck as orderPrecheck } from '../api/orders'
import { useRouter, useRoute } from 'vue-router'
import { contacts as apiContacts, list as apiMsgList, send as apiSend, markRead as apiMarkRead } from '../api/messages'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const active = ref('profile')

const profileRef = ref()
const profile = reactive({ nickname: '', signature: '', avatarUrl: '' })
const profileRules = {
  nickname: [{ required: true, message: '请填写完整信息', trigger: 'blur' }],
}
const avatarList = ref([])
const uploadAvatarRef = ref()
const savingProfile = ref(false)

async function loadProfile() {
  try {
    const res = await getMe()
    const d = res.data || {}
    profile.nickname = d.nickname || ''
    profile.signature = d.signature || ''
    profile.avatarUrl = d.avatarUrl || ''
    if (d.avatarUrl) { userStore.avatarUrl = d.avatarUrl }
    avatarList.value = profile.avatarUrl ? [{ name: 'avatar', url: profile.avatarUrl }] : []
  } catch (e) {
    avatarList.value = []
  }
}

async function doUploadAvatar(options) {
  const { file, onError, onSuccess } = options
  if (!file) return onError && onError(new Error('无文件'))
  if (file.size > 5 * 1024 * 1024) return onError && onError(new Error('图片大小不能超过 5MB'))
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await uploadFile(fd)
    const url = (res?.data && (res.data.url || (Array.isArray(res.data.urls) ? res.data.urls[0] : null))) || (typeof res?.data === 'string' ? res.data : null)
    if (!url) throw new Error('上传失败')
    avatarList.value = [{ name: file.name, url }]
    profile.avatarUrl = url
    onSuccess && onSuccess({ url })
  } catch (e) {
    ElMessage.error(e?.message || '上传失败')
    onError && onError(e)
  }
}

function onRemoveAvatar() {
  avatarList.value = []
  profile.avatarUrl = ''
}

function onAvatarExceed() {
  ElMessage.warning('最多上传1张头像')
}

async function saveProfile() {
  let ok = true
  try { await profileRef.value.validate() } catch { ok = false }
  if (!ok) { ElMessage.warning('请完善表单后再提交'); return }
  savingProfile.value = true
  try {
    await updateProfile({ nickname: profile.nickname, avatarUrl: profile.avatarUrl, signature: profile.signature })
    ElMessage.success('保存成功')
    userStore.nickname = profile.nickname
    userStore.avatarUrl = profile.avatarUrl
  } catch (e) {
    ElMessage.error(e?.message || '请求失败')
  } finally {
    savingProfile.value = false
  }
}

const mineLoading = ref(false)
const mineRecords = ref([])
const mineTotal = ref(0)
const minePage = ref(1)
const mineSize = ref(10)
const minePages = ref(1)
const prodQuery = reactive({ status: null, keyword: '', sort: 'newest' })

function firstImage(item) { return Array.isArray(item.images) && item.images.length ? item.images[0] : 'https://via.placeholder.com/300x200' }
function formatPrice(p) { return Number(p).toFixed(2) }
function statusText(s) { return s===0?'待审核':s===1?'在售':s===2?'审核未通过':s===3?'已售出':'已下架' }
function canEdit(item) { return item.status===1 || item.status===2 }
function canDown(item) { return item.status===1 }
function canUp(item) { return item.status===4 }
function canDelete(item) { return item.status===0 || item.status===2 || item.status===4 }

async function fetchMine() {
  mineLoading.value = true
  try {
    const params = { page: minePage.value, size: mineSize.value, status: prodQuery.status, keyword: prodQuery.keyword, sort: prodQuery.sort }
    const res = await productsMine(params)
    const data = res.data || { records: [], total: 0, size: mineSize.value, current: minePage.value, pages: 1 }
    mineRecords.value = data.records || []
    mineTotal.value = data.total || 0
    mineSize.value = data.size || mineSize.value
    minePage.value = data.current || minePage.value
    minePages.value = data.pages || 1
  } catch (e) {
    mineRecords.value = []
    mineTotal.value = 0
    minePages.value = 1
  } finally {
    mineLoading.value = false
  }
}

function resetMine() { prodQuery.status=null; prodQuery.keyword=''; prodQuery.sort='newest'; minePage.value=1; mineSize.value=10; fetchMine() }
function onMinePage(p) { minePage.value = p; fetchMine() }
function onMineSize(s) { mineSize.value = s>50?50:s<1?10:s; minePage.value = 1; fetchMine() }

let mineKwTimer = null
watch(() => prodQuery.status, () => { minePage.value = 1; fetchMine() })
watch(() => prodQuery.sort, () => { minePage.value = 1; fetchMine() })
watch(() => prodQuery.keyword, () => { clearTimeout(mineKwTimer); mineKwTimer = setTimeout(() => { minePage.value = 1; fetchMine() }, 300) })

const editVisible = ref(false)
const editing = ref(false)
const editForm = reactive({ id: null, price: 0.01, description: '' })
const maxPrice = 100000

function openEdit(item) {
  editForm.id = item.productId
  editForm.price = item.price
  editForm.description = item.description || ''
  editVisible.value = true
}

async function submitEdit() {
  editing.value = true
  try {
    if (!editForm.price || Number(editForm.price) <= 0) { throw new Error('价格必须大于 0') }
    if (Number(editForm.price) > maxPrice) { throw new Error('价格过大') }
    await productUpdate(editForm.id, { price: editForm.price, description: editForm.description })
    ElMessage.success('保存成功')
    editVisible.value = false
    fetchMine()
  } catch (e) {
    ElMessage.error(e?.message || '请求失败')
  } finally {
    editing.value = false
  }
}

async function down(item) {
  try { await productPatchStatus(item.productId, { status: 4 }); ElMessage.success('已下架'); fetchMine() } catch (e) { ElMessage.error(e?.message || '请求失败') }
}
async function up(item) {
  try {
    if (!item.price || Number(item.price) <= 0) { throw new Error('价格必须大于 0') }
    if (Number(item.price) > maxPrice) { throw new Error('价格过大') }
    await productPatchStatus(item.productId, { status: 1 }); ElMessage.success('已上架'); fetchMine()
  } catch (e) { ElMessage.error(e?.message || '请求失败') }
}
async function del(item) {
  try {
    await ElMessageBox.confirm('确认删除该商品？', '提示')
    await productRemove(item.productId)
    ElMessage.success('删除成功')
    fetchMine()
  } catch (e) {
    if (e && e.message && e.message.indexOf('取消')>-1) return
    ElMessage.error(e?.message || '请求失败')
  }
}

const cartLoading = ref(false)
const cartRecords = ref([])
const cartTotal = ref(0)
const cartPage = ref(1)
const cartSize = ref(10)
const cartPages = ref(1)
const selectedCart = ref([])
const cartSelectedAmount = computed(() => selectedCart.value.reduce((s, r) => s + Number(r?.price || 0), 0))

async function fetchCart() {
  cartLoading.value = true
  try {
    const res = await cartItems({ page: cartPage.value, size: cartSize.value })
    const data = res.data || { records: [], total: 0, size: cartSize.value, current: cartPage.value, pages: 1 }
    cartRecords.value = data.records || []
    cartTotal.value = data.total || 0
    cartSize.value = data.size || cartSize.value
    cartPage.value = data.current || cartPage.value
    cartPages.value = data.pages || 1
  } catch (e) {
    cartRecords.value = []
    cartTotal.value = 0
    cartPages.value = 1
  } finally {
    cartLoading.value = false
  }
}

function onCartPage(p) { cartPage.value = p; fetchCart() }
function onCartSize(s) { cartSize.value = s>50?50:s<1?10:s; cartPage.value = 1; fetchCart() }
function colPrice(row, col, val) { return `￥${Number(val ?? row?.price ?? 0).toFixed(2)}` }
function colStatus(row, col, val) { const s = val ?? row?.status ?? -1; return s===1?'在售':s===0?'待审核':s===2?'审核未通过':s===3?'已售出':'已下架' }
function onCartSelectionChange(rows) { selectedCart.value = Array.isArray(rows) ? rows : [] }
function rowSelectable(row) { return !!row && row.status === 1 }
function goDetail(pid) { router.push(`/products/${pid}`) }
async function checkoutRow(row) {
  try {
    const res = await orderPrecheck({ productId: row.productId })
    if (res?.data) { ElMessage.success('预校验通过，前往确认页'); router.push({ path: '/orders/confirm', query: { productId: String(row.productId) } }) }
    else { ElMessage.error('商品已失效或被抢光') }
  } catch (e) { ElMessage.error(e?.message || '请求失败') }
}
async function checkoutSelected() {
  if (selectedCart.value.length !== 1) { ElMessage.warning('仅支持单品结算，请选择 1 件'); return }
  const row = selectedCart.value[0]
  if (row.status !== 1) { ElMessage.error('商品已失效或被抢光'); return }
  await checkoutRow(row)
}

async function cleanupCart() {
  try { await cartCleanup(); ElMessage.success('已清除失效商品'); fetchCart() } catch (e) { ElMessage.error(e?.message || '请求失败') }
}
async function removeCart(productId) {
  try { await cartRemove(productId); ElMessage.success('已移除'); fetchCart() } catch (e) { ElMessage.error(e?.message || '请求失败') }
}

const boughtLoading = ref(false)
const boughtRecords = ref([])
const boughtTotal = ref(0)
const boughtPage = ref(1)
const boughtSize = ref(10)
const boughtPages = ref(1)

async function fetchBought() {
  boughtLoading.value = true
  try {
    const res = await ordersBought({ page: boughtPage.value, size: boughtSize.value })
    const data = res.data || { records: [], total: 0, size: boughtSize.value, current: boughtPage.value, pages: 1 }
    boughtRecords.value = data.records || []
    boughtTotal.value = data.total || 0
    boughtSize.value = data.size || boughtSize.value
    boughtPage.value = data.current || boughtPage.value
    boughtPages.value = data.pages || 1
  } catch (e) {
    boughtRecords.value = []
    boughtTotal.value = 0
    boughtPages.value = 1
  } finally {
    boughtLoading.value = false
  }
}

function onBoughtPage(p) { boughtPage.value = p; fetchBought() }
function onBoughtSize(s) { boughtSize.value = s>50?50:s<1?10:s; boughtPage.value = 1; fetchBought() }
function colOrderStatus(row, col, val) { const s = val ?? row?.status ?? -1; return s===0?'待发货':s===1?'待收货':s===2?'已完成':'已取消' }

async function confirmOrder(id) {
  try {
    await ElMessageBox.confirm('确认收货后订单将完成', '提示')
    await orderConfirm(id)
    ElMessage.success('已确认收货')
    fetchBought()
  } catch (e) {
    if (e && e.message && e.message.indexOf('取消')>-1) return
    ElMessage.error(e?.message || '请求失败')
  }
}

async function cancelOrder(id) {
  try {
    await ElMessageBox.confirm('确认取消该订单？仅待发货可取消', '提示')
    await (await import('../api/orders')).cancel(id)
    ElMessage.success('已取消订单')
    fetchBought()
  } catch (e) {
    if (e && e.message && e.message.indexOf('取消')>-1) return
    ElMessage.error(e?.message || '请求失败')
  }
}

const soldLoading = ref(false)
const soldRecords = ref([])
const soldTotal = ref(0)
const soldPage = ref(1)
const soldSize = ref(10)
const soldPages = ref(1)
const shipVisible = ref(false)
const shipping = ref(false)
const trackingNumber = ref('')
const shipOrderId = ref(null)

async function fetchSold() {
  soldLoading.value = true
  try {
    const res = await ordersSold({ page: soldPage.value, size: soldSize.value })
    const data = res.data || { records: [], total: 0, size: soldSize.value, current: soldPage.value, pages: 1 }
    soldRecords.value = data.records || []
    soldTotal.value = data.total || 0
    soldSize.value = data.size || soldSize.value
    soldPage.value = data.current || soldPage.value
    soldPages.value = data.pages || 1
  } catch (e) {
    soldRecords.value = []
    soldTotal.value = 0
    soldPages.value = 1
  } finally {
    soldLoading.value = false
  }
}

function onSoldPage(p) { soldPage.value = p; fetchSold() }
function onSoldSize(s) { soldSize.value = s>50?50:s<1?10:s; soldPage.value = 1; fetchSold() }

function openShip(row) { shipOrderId.value = row.orderId; trackingNumber.value = ''; shipVisible.value = true }
async function submitShip() {
  if (!shipOrderId.value || !trackingNumber.value) { ElMessage.warning('请填写物流单号'); return }
  shipping.value = true
  try {
    await orderShip(shipOrderId.value, { trackingNumber: trackingNumber.value })
    ElMessage.success('已发货')
    shipVisible.value = false
    fetchSold()
  } catch (e) {
    ElMessage.error(e?.message || '请求失败')
  } finally {
    shipping.value = false
  }
}

const contactsList = ref([])
const contactId = ref(null)
const messagesList = ref([])
const sendContent = ref('')
const contactNames = ref({})
const messagesSbRef = ref()
const defaultAvatar = 'https://via.placeholder.com/40?text=U'

async function refreshContacts() {
  try { const res = await apiContacts(); contactsList.value = res.data || [] } catch { contactsList.value = [] }
}
function selectContact(id) { contactId.value = id; refreshMessages() }
async function refreshMessages() {
  if (!contactId.value) { messagesList.value = []; return }
  try {
    const res = await apiMsgList({ contactId: contactId.value, page: 1, size: 20 })
    const arr = res.data?.records || res.data || []
    messagesList.value = Array.isArray(arr) ? arr.slice().sort((a,b) => new Date(a?.createdAt||0) - new Date(b?.createdAt||0)) : []
    try {
      const ids = (Array.isArray(messagesList.value) ? messagesList.value : []).filter((m) => m && m.messageId && (m.isRead === false || typeof m.isRead === 'undefined') && m.receiverId === userStore.userId).map((m) => m.messageId)
      for (const id of ids) { try { await apiMarkRead(id) } catch (e) { void e } }
    } catch (e) { void e }
    await nextTick()
    try { const wrap = messagesSbRef.value?.wrapRef; if (wrap) wrap.scrollTop = wrap.scrollHeight } catch (e) { void e }
  } catch { messagesList.value = [] }
}
async function sendMessage() {
  if (!contactId.value || !sendContent.value) return
  try { await apiSend({ receiverId: contactId.value, content: sendContent.value }); sendContent.value=''; await refreshMessages(); await nextTick(); try { const wrap = messagesSbRef.value?.wrapRef; if (wrap) wrap.scrollTop = wrap.scrollHeight } catch (e) { void e } } catch (e) { ElMessage.error(e?.message || '请求失败') }
}

onMounted(() => {
  const initTab = (route.query?.tab === 'messages' || route.query?.tab === 'products' || route.query?.tab === 'cart' || route.query?.tab === 'bought' || route.query?.tab === 'sold' || route.query?.tab === 'profile') ? route.query.tab : 'profile'
  active.value = initTab
  const initContact = route.query?.contactId ? Number(route.query.contactId) : null
  const initContactName = route.query?.contactName ? String(route.query.contactName) : ''
  if (initContact) { contactId.value = initContact }
  if (initContact && initContactName) { contactNames.value[initContact] = initContactName }
  loadProfile()
  fetchMine()
  fetchCart()
  fetchBought()
  fetchSold()
  refreshContacts()
  if (active.value === 'messages') { refreshMessages() }
})

function contactDisplayName(c) {
  const id = c?.contactId
  const name = c?.contactNickname || (id ? contactNames.value[id] : '')
  return name || `联系人 ${id}`
}
</script>

<style scoped>
.wrap { padding: 16px; display: flex; justify-content: center; }
.card { width: 100%; max-width: 1080px; }
.head { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.meta { display: flex; flex-direction: column; }
.nick { font-weight: 600; }
.uid { color: #888; font-size: 12px; }
.bar { display: flex; gap: 12px; align-items: center; margin-bottom: 12px; flex-wrap: wrap; }
.list { min-height: 160px; }
.card2 { margin-bottom: 12px; }
.img { width: 100%; height: 140px; object-fit: cover; border-radius: 4px; }
.info { display: flex; align-items: center; justify-content: space-between; margin-top: 8px; }
.title { font-size: 14px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 65%; }
.price { color: #e74c3c; font-weight: 600; }
.ops { display: flex; gap: 8px; align-items: center; margin-top: 8px; flex-wrap: wrap; }
.pager { margin-top: 12px; display: flex; justify-content: center; }
.summary { margin-top: 12px; display: flex; justify-content: space-between; align-items: center; }
.msg { display: grid; grid-template-columns: 280px 1fr; gap: 12px; }
.left { border-right: 1px solid #eee; padding-right: 12px; }
.list3 { height: 360px; }
.item3 { padding: 8px; border-radius: 4px; cursor: pointer; }
.row3 { display: flex; align-items: center; gap: 8px; }
.item3.active { background: #f5f7fa; }
.name { font-weight: 600; }
.time { color: #888; font-size: 12px; }
.right { display: flex; flex-direction: column; }
.list4 { height: 360px; }
.item4 { display: flex; align-items: flex-end; margin: 8px 0; gap: 8px; }
.item4.mine { flex-direction: row-reverse; }
.avatar { flex: 0 0 auto; }
.bubble { max-width: 70%; padding: 8px 10px; border-radius: 12px; background: #f5f7fa; }
.item4.mine .bubble { background: #cfe8ff; }
.text { white-space: pre-wrap; word-break: break-word; }
.time { color: #888; font-size: 12px; margin-top: 4px; }
.send { display: flex; gap: 8px; margin-top: 12px; }
</style>
