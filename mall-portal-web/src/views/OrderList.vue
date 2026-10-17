<template>
  <div class="container order-page">
    <h2 class="page-title">我的订单</h2>

    <el-tabs v-model="activeStatus" @tab-change="load">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane label="待发货" name="1" />
      <el-tab-pane label="待收货" name="2" />
      <el-tab-pane label="已完成" name="3" />
    </el-tabs>

    <div v-if="orders.length" class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-head">
          <span>订单号：{{ order.orderNo }}</span>
          <span class="time">{{ (order.createTime || '').replace('T', ' ').slice(0, 16) }}</span>
          <el-tag :type="statusType(order.status)" size="small">{{ order.statusDesc }}</el-tag>
        </div>

        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="item-row">
            <el-image :src="item.productImage || PLACEHOLDER_IMG" fit="cover" class="thumb" />
            <router-link :to="`/product/detail/${item.productId}`" class="i-name">{{ item.productName }}</router-link>
            <span class="i-price">¥{{ formatPrice(item.price) }} ×{{ item.quantity }}</span>
          </div>
        </div>

        <div class="order-foot">
          <span class="amount">
            实付：<span class="price">¥{{ formatPrice(order.payAmount) }}</span>
          </span>
          <div class="actions">
            <el-button v-if="order.status === 0" type="primary" size="small" @click="$router.push(`/order/pay/${order.id}`)">
              去支付
            </el-button>
            <el-button v-if="order.status === 2" type="primary" size="small" @click="onReceive(order)">
              确认收货
            </el-button>
            <el-button v-if="order.status === 3" size="small" @click="openComment(order)">
              评价
            </el-button>
            <el-button v-if="[1, 2, 3].includes(order.status)" size="small" @click="openAfterSale(order)">
              申请售后
            </el-button>
            <el-button size="small" @click="$router.push('/chat')">
              联系客服
            </el-button>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无订单" />

    <!-- 评价弹窗 -->
    <el-dialog v-model="commentVisible" title="发表评价" width="480px">
      <el-form label-width="80px">
        <el-form-item label="商品">
          <span>{{ commentProduct?.productName }}</span>
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="commentForm.rating" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="commentForm.content" type="textarea" :rows="4" placeholder="说说你的使用体验" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="commentVisible = false">取消</el-button>
        <el-button type="primary" :loading="commentLoading" @click="submitComment">提交</el-button>
      </template>
    </el-dialog>

    <!-- 售后申请弹窗 -->
    <el-dialog v-model="afterSaleVisible" title="申请售后" width="520px">
      <el-form label-width="90px">
        <el-form-item label="售后类型">
          <el-radio-group v-model="afterSaleForm.type">
            <el-radio :value="1">申请退款</el-radio>
            <el-radio :value="2">申请发票</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="afterSaleForm.type === 1">
          <el-form-item label="退款原因">
            <el-input v-model="afterSaleForm.reason" type="textarea" :rows="3" placeholder="请描述退款原因" />
          </el-form-item>
          <el-form-item label="凭证图片">
            <el-upload
              action="/api/portal/upload"
              :headers="uploadHeaders"
              list-type="picture-card"
              :limit="3"
              :on-success="onUploadSuccess"
              :on-remove="onUploadRemove"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="抬头类型">
            <el-radio-group v-model="afterSaleForm.invoiceType">
              <el-radio :value="1">个人</el-radio>
              <el-radio :value="2">公司</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="发票抬头">
            <el-input v-model="afterSaleForm.invoiceTitle" placeholder="请输入发票抬头" />
          </el-form-item>
          <el-form-item v-if="afterSaleForm.invoiceType === 2" label="税号">
            <el-input v-model="afterSaleForm.taxNo" placeholder="请输入税号" />
          </el-form-item>
          <el-form-item label="接收邮箱">
            <el-input v-model="afterSaleForm.email" placeholder="请输入接收邮箱" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="afterSaleVisible = false">取消</el-button>
        <el-button type="primary" :loading="afterSaleLoading" @click="submitAfterSale">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, receiveOrder, publishComment, applyRefund, applyInvoice } from '@/api'
import { formatPrice, PLACEHOLDER_IMG } from '@/utils'

const activeStatus = ref('')
const orders = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const commentVisible = ref(false)
const commentLoading = ref(false)
const commentOrder = ref(null)
const commentProduct = ref(null)
const commentForm = ref({ rating: 5, content: '' })

const statusType = (s) => {
  return ['warning', 'primary', 'info', 'success', 'danger'][s] || 'info'
}

const load = async () => {
  const params = { pageNum: pageNum.value, pageSize: pageSize.value }
  if (activeStatus.value !== '') params.status = Number(activeStatus.value)
  const page = await getOrderList(params)
  orders.value = page.records || []
  total.value = Number(page.total || 0)
}

const onReceive = async (order) => {
  await ElMessageBox.confirm('确认已收到货吗？', '提示', { type: 'warning' })
  await receiveOrder(order.id)
  ElMessage.success('已确认收货')
  load()
}

const openComment = (order) => {
  // 对订单里的每个商品分别评价，这里取第一个未评价的简化处理
  commentOrder.value = order
  commentProduct.value = order.items[0]
  commentForm.value = { rating: 5, content: '' }
  commentVisible.value = true
}

const submitComment = async () => {
  commentLoading.value = true
  try {
    await publishComment({
      orderId: commentOrder.value.id,
      productId: commentProduct.value.productId,
      content: commentForm.value.content,
      rating: commentForm.value.rating
    })
    ElMessage.success('评价成功')
    commentVisible.value = false
  } finally {
    commentLoading.value = false
  }
}

// ===== 申请售后 =====
const afterSaleVisible = ref(false)
const afterSaleLoading = ref(false)
const afterSaleOrder = ref(null)
const afterSaleForm = reactive({ type: 1, reason: '', invoiceType: 1, invoiceTitle: '', taxNo: '', email: '' })
const afterSaleImages = ref([])
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${sessionStorage.getItem('token')}` }))

const openAfterSale = (order) => {
  afterSaleOrder.value = order
  afterSaleForm.type = 1
  afterSaleForm.reason = ''
  afterSaleForm.invoiceType = 1
  afterSaleForm.invoiceTitle = ''
  afterSaleForm.taxNo = ''
  afterSaleForm.email = ''
  afterSaleImages.value = []
  afterSaleVisible.value = true
}

const onUploadSuccess = (response) => {
  if (response?.code === 200 && response.data) afterSaleImages.value.push(response.data)
}
const onUploadRemove = (file) => {
  const url = file.response?.data
  if (url) afterSaleImages.value = afterSaleImages.value.filter((u) => u !== url)
}

const submitAfterSale = async () => {
  afterSaleLoading.value = true
  try {
    const base = { orderId: afterSaleOrder.value.id, reason: afterSaleForm.reason, images: afterSaleImages.value.join(',') }
    if (afterSaleForm.type === 1) {
      await applyRefund(base)
      ElMessage.success('退款申请已提交')
    } else {
      await applyInvoice({
        ...base,
        invoiceType: afterSaleForm.invoiceType,
        invoiceTitle: afterSaleForm.invoiceTitle,
        taxNo: afterSaleForm.taxNo,
        email: afterSaleForm.email
      })
      ElMessage.success('发票申请已提交')
    }
    afterSaleVisible.value = false
  } finally {
    afterSaleLoading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.order-page {
  margin-top: 20px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #333;
}
.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.order-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid var(--mall-border);
  overflow: hidden;
}
.order-head {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 20px;
  background: #fafafa;
  color: #666;
  font-size: 13px;
  border-bottom: 1px solid var(--mall-border);
}
.time {
  margin-left: auto;
  color: #999;
}
.order-items {
  padding: 8px 20px;
}
.item-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}
.thumb {
  width: 56px;
  height: 56px;
  border-radius: 6px;
}
.i-name {
  flex: 1;
  color: #333;
  font-size: 14px;
}
.i-price {
  color: #666;
  font-size: 13px;
}
.order-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  border-top: 1px solid var(--mall-border);
}
.amount {
  color: #666;
  font-size: 13px;
}
.amount .price {
  font-size: 18px;
}
.actions {
  display: flex;
  gap: 8px;
}
</style>
