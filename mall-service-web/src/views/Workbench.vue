<template>
  <div class="workbench">
    <!-- 左栏：会话 / 售后单列表 -->
    <aside class="left">
      <el-tabs v-model="leftTab" class="tabs">
        <el-tab-pane label="会话" name="chat">
          <div class="conv-list">
            <div
              v-for="c in conversations"
              :key="c.id"
              class="conv-item"
              :class="{ active: selectedConversation?.id === c.id && leftTab === 'chat' }"
              @click="selectConversation(c)"
            >
              <div class="conv-head">
                <span class="nick">{{ c.memberNickname || '会员' + c.memberId }}</span>
                <el-tag :type="c.status === 0 ? 'warning' : 'success'" size="small">{{ c.statusDesc }}</el-tag>
              </div>
              <div class="last">{{ c.lastMessage || '暂无消息' }}</div>
            </div>
            <el-empty v-if="!conversations.length" description="暂无会话" :image-size="60" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="售后单" name="afterSale">
          <div class="as-filter">
            <el-select v-model="asQuery.type" clearable placeholder="类型" size="small" @change="loadAfterSales">
              <el-option label="退款" :value="1" />
              <el-option label="发票" :value="2" />
              <el-option label="纠纷" :value="3" />
            </el-select>
            <el-select v-model="asQuery.status" clearable placeholder="状态" size="small" @change="loadAfterSales">
              <el-option label="待处理" :value="0" />
              <el-option label="处理中" :value="1" />
              <el-option label="已完成" :value="2" />
              <el-option label="已拒绝" :value="3" />
            </el-select>
          </div>
          <div class="as-list">
            <div
              v-for="a in afterSales"
              :key="a.id"
              class="as-item"
              :class="{ active: selectedAfterSale?.id === a.id }"
              @click="selectAfterSale(a)"
            >
              <div class="as-head">
                <el-tag size="small" :type="typeTag(a.type)">{{ a.typeDesc }}</el-tag>
                <el-tag size="small" :type="statusTag(a.status)">{{ a.statusDesc }}</el-tag>
              </div>
              <div class="as-no">{{ a.afterSaleNo }}</div>
              <div class="as-meta">{{ a.memberNickname || '会员' + a.memberId }} · {{ a.orderNo }}</div>
            </div>
            <el-empty v-if="!afterSales.length" description="暂无售后单" :image-size="60" />
          </div>
          <div class="pager">
            <el-pagination
              small
              background
              layout="prev, next, total"
              :total="asTotal"
              :page-size="asQuery.pageSize"
              :current-page="asQuery.pageNum"
              @current-change="onAsPage"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </aside>

    <!-- 右栏 -->
    <section class="right">
      <!-- 聊天面板 -->
      <template v-if="leftTab === 'chat' && selectedConversation">
        <div class="chat-panel">
          <div class="chat-head">
            <span>{{ selectedConversation.memberNickname || '会员' + selectedConversation.memberId }}</span>
            <div>
              <el-button v-if="selectedConversation.status === 0" size="small" type="primary" @click="claim(selectedConversation)">接入</el-button>
              <el-button v-else-if="selectedConversation.status === 1" size="small" @click="onEnd">结束会话</el-button>
            </div>
          </div>
          <div ref="chatBodyRef" class="chat-body">
            <div v-for="m in messages" :key="m.id" class="msg-row" :class="m.senderType === 'SERVICE' ? 'mine' : 'other'">
              <template v-if="m.type === 'IMAGE'">
                <el-image :src="m.content" fit="cover" class="msg-img" :preview-src-list="[m.content]" />
              </template>
              <template v-else-if="m.type === 'PRODUCT'">
                <div class="msg-product">
                  <el-image v-if="m.productImage" :src="m.productImage" fit="cover" class="p-img" />
                  <div v-else class="p-img p-img-empty">无图</div>
                  <div class="p-info">
                    <div class="p-name">{{ m.content }}</div>
                    <div class="p-tip">商品咨询</div>
                  </div>
                </div>
              </template>
              <template v-else-if="m.type === 'ORDER'">
                <div class="order-card">
                  <div class="oc-head">
                    <span class="oc-no">{{ parseOrder(m).orderNo }}</span>
                    <el-tag size="small" :type="orderStatusTag(parseOrder(m).status)">{{ parseOrder(m).statusDesc }}</el-tag>
                  </div>
                  <div class="oc-items">
                    <div v-for="(it, i) in parseOrder(m).items" :key="i" class="oc-item">
                      <el-image v-if="it.productImage" :src="it.productImage" fit="cover" class="oc-img" />
                      <div v-else class="oc-img oc-img-empty">无图</div>
                      <div class="oc-name">{{ it.productName }}</div>
                      <div class="oc-price">¥{{ formatPrice(it.price) }} ×{{ it.quantity }}</div>
                    </div>
                  </div>
                  <div class="oc-foot">
                    <span>实付</span>
                    <span class="oc-total">¥{{ formatPrice(parseOrder(m).payAmount) }}</span>
                  </div>
                </div>
              </template>
              <template v-else>
                <div class="bubble">{{ m.content }}</div>
              </template>
            </div>
            <el-empty v-if="!messages.length" description="开始对话吧" :image-size="80" />
          </div>
          <div v-if="selectedConversation.status === 1" class="chat-input">
            <el-input
              v-model="messageInput"
              type="textarea"
              :rows="2"
              placeholder="输入回复内容，回车发送"
              @keydown.enter.prevent="onSend"
            />
            <el-button type="primary" @click="onSend">发送</el-button>
          </div>
        </div>
      </template>
      <div v-else-if="leftTab === 'chat'" class="placeholder">
        <el-empty description="选择一个会话开始接待" />
      </div>

      <!-- 售后单详情 -->
      <template v-if="leftTab === 'afterSale' && selectedAfterSale">
        <div class="as-detail panel-card">
          <h3 class="dt-title">售后单详情</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="售后单号">{{ selectedAfterSale.afterSaleNo }}</el-descriptions-item>
            <el-descriptions-item label="类型">
              <el-tag size="small" :type="typeTag(selectedAfterSale.type)">{{ selectedAfterSale.typeDesc }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag size="small" :type="statusTag(selectedAfterSale.status)">{{ selectedAfterSale.statusDesc }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="会员">{{ selectedAfterSale.memberNickname || '会员' + selectedAfterSale.memberId }}</el-descriptions-item>
            <el-descriptions-item label="订单号">{{ selectedAfterSale.orderNo }}</el-descriptions-item>
            <el-descriptions-item v-if="selectedAfterSale.type !== 2" label="退款金额">¥{{ formatPrice(selectedAfterSale.refundAmount) }}</el-descriptions-item>
            <el-descriptions-item label="申请原因" :span="2">{{ selectedAfterSale.reason || '—' }}</el-descriptions-item>
            <template v-if="selectedAfterSale.type === 2">
              <el-descriptions-item label="抬头类型">{{ selectedAfterSale.invoiceType === 1 ? '个人' : '公司' }}</el-descriptions-item>
              <el-descriptions-item label="发票抬头">{{ selectedAfterSale.invoiceTitle || '—' }}</el-descriptions-item>
              <el-descriptions-item label="税号">{{ selectedAfterSale.taxNo || '—' }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ selectedAfterSale.email || '—' }}</el-descriptions-item>
              <el-descriptions-item label="发票号码">{{ selectedAfterSale.invoiceNo || '—' }}</el-descriptions-item>
            </template>
            <el-descriptions-item label="凭证图片" :span="2">
              <div v-if="images.length" class="imgs">
                <el-image v-for="img in images" :key="img" :src="img" fit="cover" class="img" :preview-src-list="images" />
              </div>
              <span v-else>—</span>
            </el-descriptions-item>
            <el-descriptions-item label="处理说明" :span="2">{{ selectedAfterSale.handleRemark || '—' }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ (selectedAfterSale.createTime || '').replace('T', ' ').slice(0, 16) }}</el-descriptions-item>
            <el-descriptions-item label="处理客服">{{ selectedAfterSale.serviceNickname || '—' }}</el-descriptions-item>
          </el-descriptions>

          <div class="dt-actions">
            <el-button v-if="selectedAfterSale.status === 0" type="primary" @click="onAccept">接单</el-button>
            <template v-if="selectedAfterSale.status === 1">
              <el-button type="success" @click="openHandle('complete')">办结</el-button>
              <el-button type="danger" @click="openHandle('reject')">拒绝</el-button>
            </template>
          </div>
        </div>
      </template>
      <div v-else-if="leftTab === 'afterSale'" class="placeholder">
        <el-empty description="选择一条售后单处理" />
      </div>
    </section>

    <!-- 处理弹窗 -->
    <el-dialog v-model="handleDialog.visible" :title="handleDialog.mode === 'complete' ? '办结售后单' : '拒绝售后单'" width="440px">
      <el-form label-width="80px">
        <el-form-item label="处理说明">
          <el-input v-model="handleDialog.remark" type="textarea" :rows="3" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Client } from '@stomp/stompjs'
import {
  getConversationList,
  claimConversation,
  getConversationMessages,
  sendConversationMessage,
  endConversation,
  getAfterSalePage,
  acceptAfterSale,
  completeAfterSale,
  rejectAfterSale
} from '@/api'

const formatPrice = (v) => Number(v ?? 0).toFixed(2)

const leftTab = ref('chat')

// 会话
const conversations = ref([])
const selectedConversation = ref(null)
const messages = ref([])
const messageInput = ref('')
const chatBodyRef = ref()
let stompClient = null
let currentSub = null
let pollTimer = null

// 售后单
const afterSales = ref([])
const asTotal = ref(0)
const asQuery = reactive({ type: null, status: null, pageNum: 1, pageSize: 10 })
const selectedAfterSale = ref(null)
const handleDialog = reactive({ visible: false, mode: 'complete', remark: '', invoiceNo: '' })

const images = computed(() => {
  const imgs = selectedAfterSale.value?.images
  if (!imgs) return []
  return imgs.split(',').filter(Boolean)
})

const typeTag = (t) => ({ 1: 'danger', 2: 'primary', 3: 'warning' }[t] || 'info')
const statusTag = (s) => ({ 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger', 4: 'info' }[s] || 'info')
const orderStatusTag = (s) => ['warning', 'primary', 'info', 'success', 'danger', 'danger'][s] || 'info'

const parseOrder = (m) => {
  try {
    const o = JSON.parse(m.orderSnapshot || '{}')
    return o && o.orderNo ? o : { orderNo: m.content, status: m.orderStatus, statusDesc: m.orderStatusDesc, payAmount: 0, items: [] }
  } catch (e) {
    return { orderNo: m.content, status: m.orderStatus, statusDesc: m.orderStatusDesc, payAmount: 0, items: [] }
  }
}

// ===== 会话 =====
const loadConversations = async () => {
  conversations.value = await getConversationList()
}

const selectConversation = async (c) => {
  selectedConversation.value = c
  if (c.status === 0) {
    messages.value = []
    return
  }
  messages.value = await getConversationMessages(c.id)
  scrollToBottom()
  subscribeConversation(c.id)
}

const claim = async (c) => {
  await claimConversation(c.id)
  ElMessage.success('已接入')
  await loadConversations()
  const updated = conversations.value.find((x) => x.id === c.id)
  if (updated) selectConversation(updated)
}

const onEnd = async () => {
  await ElMessageBox.confirm('确认结束该会话吗？', '提示', { type: 'warning' })
  await endConversation(selectedConversation.value.id)
  ElMessage.success('会话已结束')
  selectedConversation.value = null
  messages.value = []
  loadConversations()
}

const onSend = async () => {
  const content = messageInput.value.trim()
  if (!content || !selectedConversation.value) return
  const id = selectedConversation.value.id
  messageInput.value = ''
  const msg = await sendConversationMessage(id, { content })
  appendMessage(msg)
}

const appendMessage = (msg) => {
  if (!messages.value.some((m) => m.id === msg.id)) {
    messages.value.push(msg)
  }
  scrollToBottom()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBodyRef.value) chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
  })
}

// ===== WebSocket =====
const connectStomp = () => {
  const token = sessionStorage.getItem('token')
  const proto = window.location.protocol === 'https:' ? 'wss' : 'ws'
  const wsUrl = `${proto}://${window.location.host}/ws?token=${token}`
  stompClient = new Client({
    brokerURL: wsUrl,
    reconnectDelay: 5000,
    debug: () => {},
    onConnect: () => {
      if (selectedConversation.value) subscribeConversation(selectedConversation.value.id)
    }
  })
  stompClient.activate()
}

const subscribeConversation = (id) => {
  if (!stompClient || !stompClient.connected) return
  if (currentSub) currentSub.unsubscribe()
  currentSub = stompClient.subscribe(`/topic/conversation/${id}`, (frame) => {
    try {
      appendMessage(JSON.parse(frame.body))
    } catch (e) {
      /* ignore */
    }
  })
}

// ===== 售后单 =====
const loadAfterSales = async () => {
  const params = { pageNum: asQuery.pageNum, pageSize: asQuery.pageSize }
  if (asQuery.type) params.type = asQuery.type
  if (asQuery.status !== null && asQuery.status !== '') params.status = asQuery.status
  const page = await getAfterSalePage(params)
  afterSales.value = page.records || []
  asTotal.value = Number(page.total || 0)
}

const onAsPage = (p) => {
  asQuery.pageNum = p
  loadAfterSales()
}

const selectAfterSale = (a) => {
  selectedAfterSale.value = a
}

const onAccept = async () => {
  await acceptAfterSale(selectedAfterSale.value.id)
  ElMessage.success('已接单')
  selectedAfterSale.value.status = 1
  loadAfterSales()
}

const openHandle = (mode) => {
  handleDialog.visible = true
  handleDialog.mode = mode
  handleDialog.remark = ''
  handleDialog.invoiceNo = ''
}

const submitHandle = async () => {
  const d = { handleRemark: handleDialog.remark, invoiceNo: handleDialog.invoiceNo }
  if (handleDialog.mode === 'complete') {
    await completeAfterSale(selectedAfterSale.value.id, d)
  } else {
    await rejectAfterSale(selectedAfterSale.value.id, d)
  }
  ElMessage.success('操作成功')
  handleDialog.visible = false
  selectedAfterSale.value = null
  loadAfterSales()
}

onMounted(() => {
  loadConversations()
  loadAfterSales()
  connectStomp()
  pollTimer = setInterval(loadConversations, 3000)
})

onBeforeUnmount(() => {
  if (pollTimer) clearInterval(pollTimer)
  if (currentSub) currentSub.unsubscribe()
  if (stompClient) stompClient.deactivate()
})
</script>

<style scoped>
.workbench {
  height: calc(100vh - 60px);
  display: flex;
}
.left {
  width: 320px;
  background: #111827;
  border-right: 1px solid #1f2937;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 12px;
}
.tabs :deep(.el-tabs__content) {
  flex: 1;
  overflow-y: auto;
}
.conv-list,
.as-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.conv-item,
.as-item {
  padding: 12px;
  border: 1px solid #1f2937;
  border-radius: 8px;
  cursor: pointer;
  background: #1b2336;
}
.conv-item.active,
.as-item.active {
  border-color: #ff5000;
}
.conv-head,
.as-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}
.nick {
  font-size: 14px;
  color: #e2e8f0;
  font-weight: 600;
}
.last {
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.as-no {
  margin-top: 6px;
  font-size: 12px;
  color: #94a3b8;
}
.as-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
}
.as-filter {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.pager {
  margin-top: 12px;
  display: flex;
  justify-content: center;
}

.right {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.placeholder {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.chat-head {
  height: 56px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #1f2937;
  background: #111827;
  color: #e2e8f0;
  font-weight: 600;
}
.chat-body {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.msg-row {
  display: flex;
}
.msg-row.mine {
  justify-content: flex-end;
}
.msg-row.other {
  justify-content: flex-start;
}
.bubble {
  max-width: 60%;
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}
.msg-row.mine .bubble {
  background: #ff5000;
  color: #fff;
}
.msg-row.other .bubble {
  background: #273244;
  color: #e2e8f0;
}
.msg-img {
  max-width: 200px;
  border-radius: 8px;
  display: block;
}
.msg-product {
  display: flex;
  gap: 10px;
  align-items: center;
  background: #1b2336;
  border: 1px solid #334155;
  border-radius: 8px;
  padding: 8px;
}
.p-img {
  width: 56px;
  height: 56px;
  border-radius: 6px;
  flex-shrink: 0;
}
.p-img-empty {
  background: #334155;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-size: 12px;
}
.p-name {
  font-weight: 600;
}
.p-tip {
  margin-top: 4px;
  font-size: 12px;
  opacity: 0.7;
}
.msg-order {
  display: flex;
  align-items: center;
  gap: 8px;
}
.o-no {
  font-weight: 600;
}
.order-card {
  max-width: 260px;
  background: #1b2336;
  border: 1px solid #334155;
  border-radius: 8px;
  overflow: hidden;
  font-size: 12px;
}
.oc-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 8px 12px;
  border-bottom: 1px solid #334155;
  background: #111827;
}
.oc-no {
  color: #94a3b8;
}
.oc-items {
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.oc-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.oc-img {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  flex-shrink: 0;
}
.oc-img-empty {
  background: #334155;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
}
.oc-name {
  flex: 1;
  color: #e2e8f0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.oc-price {
  color: #94a3b8;
}
.oc-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  border-top: 1px solid #334155;
  color: #94a3b8;
}
.oc-total {
  color: #ff5000;
  font-weight: 700;
  font-size: 14px;
}
.chat-input {
  padding: 12px 20px;
  border-top: 1px solid #1f2937;
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.as-detail {
  margin: 20px;
  overflow-y: auto;
}
.dt-title {
  font-size: 16px;
  font-weight: 600;
  color: #e2e8f0;
  margin-bottom: 16px;
}
.dt-actions {
  margin-top: 20px;
  display: flex;
  gap: 12px;
}
.imgs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.img {
  width: 80px;
  height: 80px;
  border-radius: 6px;
}
</style>
