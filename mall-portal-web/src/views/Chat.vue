<template>
  <div class="container chat-page">
    <div class="chat-layout">
      <aside class="conv-panel">
        <div class="conv-head">
          <span>我的会话</span>
          <el-button size="small" type="primary" @click="onCreate">联系客服</el-button>
        </div>
        <div class="conv-list">
          <div
            v-for="c in conversations"
            :key="c.id"
            class="conv-item"
            :class="{ active: selectedConversation?.id === c.id }"
            @click="selectConversation(c)"
          >
            <div class="conv-title">
              <span>{{ c.serviceNickname || '在线客服' }}</span>
              <el-tag v-if="c.status === 2" size="small" type="info">已结束</el-tag>
            </div>
            <div class="conv-last">{{ c.lastMessage || '暂无消息' }}</div>
          </div>
          <el-empty v-if="!conversations.length" description="暂无会话" :image-size="60" />
        </div>
      </aside>

      <section class="chat-panel">
        <template v-if="selectedConversation">
          <div class="chat-title">{{ selectedConversation.serviceNickname || '在线客服' }}</div>
          <div ref="bodyRef" class="chat-body">
            <div v-for="m in messages" :key="m.id" class="msg-row" :class="m.senderType === 'MEMBER' ? 'mine' : 'other'">
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
          <div v-if="selectedConversation.status !== 2" class="chat-input">
            <el-popover v-model:visible="toolVisible" placement="top-end" :width="160" trigger="click" :show-arrow="false">
              <template #reference>
                <el-button circle :icon="Plus" />
              </template>
              <div class="tool-menu">
                <el-upload
                  action="/api/portal/upload"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  accept="image/*"
                  :on-success="onImageUploaded"
                >
                  <div class="tool-item"><el-icon><Picture /></el-icon>图片</div>
                </el-upload>
                <div class="tool-item" @click="onPickOrderFromTool"><el-icon><Document /></el-icon>选择订单</div>
              </div>
            </el-popover>
            <el-input v-model="messageInput" type="textarea" :rows="2" placeholder="请输入内容，回车发送" @keydown.enter.prevent="onSend" />
            <el-button type="primary" @click="onSend">发送</el-button>
          </div>
        </template>
        <div v-else class="chat-empty">
          <el-empty description="点击「联系客服」开始咨询" />
        </div>
      </section>
    </div>

    <!-- 选择订单弹窗 -->
    <el-dialog v-model="orderPickerVisible" title="选择订单" width="520px">
      <div v-if="orders.length" class="pick-list">
        <div v-for="o in orders" :key="o.id" class="pick-item" @click="onPickOrder(o)">
          <div class="pick-no">{{ o.orderNo }}</div>
          <el-tag size="small" :type="orderStatusTag(o.status)">{{ o.statusDesc }}</el-tag>
          <span class="pick-amount">¥{{ formatPrice(o.payAmount) }}</span>
        </div>
      </div>
      <el-empty v-else description="暂无订单" :image-size="60" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture, Plus, Document } from '@element-plus/icons-vue'
import { Client } from '@stomp/stompjs'
import { createConversation, getConversationList, getConversationMessages, sendConversationMessage, getOrderList } from '@/api'
import { formatPrice } from '@/utils'

const conversations = ref([])
const selectedConversation = ref(null)
const messages = ref([])
const messageInput = ref('')
const bodyRef = ref()
let stompClient = null
let currentSub = null

const route = useRoute()
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${sessionStorage.getItem('token')}` }))
const orderPickerVisible = ref(false)
const orders = ref([])
const toolVisible = ref(false)

const loadList = async () => {
  conversations.value = await getConversationList()
}

const onCreate = async () => {
  const conv = await createConversation({})
  await loadList()
  const updated = conversations.value.find((c) => c.id === conv.id)
  if (updated) {
    await selectConversation(updated)
  } else {
    selectedConversation.value = conv
    messages.value = []
    subscribeConversation(conv.id)
  }
  ElMessage.success('已为您接入客服')
}

const selectConversation = async (c) => {
  selectedConversation.value = c
  messages.value = await getConversationMessages(c.id)
  scrollToBottom()
  subscribeConversation(c.id)
}

const sendMsg = async (convId, payload) => {
  const msg = await sendConversationMessage(convId, payload)
  appendMessage(msg)
}

const onSend = async () => {
  const content = messageInput.value.trim()
  if (!content || !selectedConversation.value) return
  messageInput.value = ''
  await sendMsg(selectedConversation.value.id, { type: 'TEXT', content })
}

const onImageUploaded = async (response) => {
  toolVisible.value = false
  if (response?.code === 200 && response.data) {
    await sendMsg(selectedConversation.value.id, { type: 'IMAGE', content: response.data })
  } else {
    ElMessage.error('图片上传失败')
  }
}

const orderStatusTag = (s) => ['warning', 'primary', 'info', 'success', 'danger', 'danger'][s] || 'info'

const parseOrder = (m) => {
  try {
    const o = JSON.parse(m.orderSnapshot || '{}')
    return o && o.orderNo ? o : { orderNo: m.content, status: m.orderStatus, statusDesc: m.orderStatusDesc, payAmount: 0, items: [] }
  } catch (e) {
    return { orderNo: m.content, status: m.orderStatus, statusDesc: m.orderStatusDesc, payAmount: 0, items: [] }
  }
}

const openOrderPicker = async () => {
  const page = await getOrderList({ pageNum: 1, pageSize: 100 })
  orders.value = page.records || []
  orderPickerVisible.value = true
}

const onPickOrderFromTool = () => {
  toolVisible.value = false
  openOrderPicker()
}

const onPickOrder = async (o) => {
  orderPickerVisible.value = false
  await sendMsg(selectedConversation.value.id, { type: 'ORDER', orderId: o.id })
}

const appendMessage = (msg) => {
  if (!messages.value.some((m) => m.id === msg.id)) {
    messages.value.push(msg)
  }
  scrollToBottom()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (bodyRef.value) bodyRef.value.scrollTop = bodyRef.value.scrollHeight
  })
}

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

onMounted(async () => {
  await loadList()
  const pid = route.query.productId
  let conv = conversations.value.find((c) => c.status !== 2)
  if (!conv && pid) {
    // 从商品详情页带商品过来：无可用会话则新建
    conv = await createConversation({})
    conversations.value.unshift(conv)
  }
  if (conv) {
    await selectConversation(conv)
  }
  connectStomp()
  if (pid && conv) {
    await sendMsg(conv.id, { type: 'PRODUCT', productId: Number(pid) })
  }
})

onBeforeUnmount(() => {
  if (currentSub) currentSub.unsubscribe()
  if (stompClient) stompClient.deactivate()
})
</script>

<style scoped>
.chat-page {
  margin-top: 20px;
}
.chat-layout {
  display: flex;
  height: 640px;
  border: 1px solid var(--mall-border);
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}
.conv-panel {
  width: 280px;
  border-right: 1px solid var(--mall-border);
  display: flex;
  flex-direction: column;
}
.conv-head {
  height: 56px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--mall-border);
  font-weight: 600;
  color: #333;
}
.conv-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}
.conv-item {
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 8px;
  border: 1px solid transparent;
}
.conv-item:hover,
.conv-item.active {
  background: #fff7f2;
  border-color: var(--mall-primary);
}
.conv-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #333;
  font-size: 14px;
  font-weight: 600;
}
.conv-last {
  margin-top: 6px;
  font-size: 12px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.chat-title {
  height: 56px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--mall-border);
  font-weight: 600;
  color: #333;
}
.chat-body {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: #fafafa;
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
  background: var(--mall-primary);
  color: #fff;
}
.msg-row.other .bubble {
  background: #fff;
  color: #333;
  border: 1px solid var(--mall-border);
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
  background: #fff;
  border: 1px solid var(--mall-border);
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
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #bbb;
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
  background: #fff;
  border: 1px solid var(--mall-border);
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
  border-bottom: 1px solid var(--mall-border);
  background: #fafafa;
}
.oc-no {
  color: #666;
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
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #bbb;
}
.oc-name {
  flex: 1;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.oc-price {
  color: #666;
}
.oc-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  border-top: 1px solid var(--mall-border);
  color: #666;
}
.oc-total {
  color: var(--mall-price);
  font-weight: 700;
  font-size: 14px;
}
.chat-input {
  padding: 12px 20px;
  border-top: 1px solid var(--mall-border);
  display: flex;
  gap: 12px;
  align-items: flex-end;
}
.chat-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.pick-list {
  max-height: 400px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.pick-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border: 1px solid var(--mall-border);
  border-radius: 8px;
  cursor: pointer;
}
.pick-item:hover {
  border-color: var(--mall-primary);
  background: #fff7f2;
}
.pick-no {
  flex: 1;
  font-size: 13px;
  color: #333;
}
.pick-amount {
  color: var(--mall-price);
  font-weight: 600;
}
.tool-menu {
  display: flex;
  flex-direction: column;
}
.tool-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  color: #333;
  font-size: 14px;
}
.tool-item:hover {
  background: #f5f6f8;
}
</style>
