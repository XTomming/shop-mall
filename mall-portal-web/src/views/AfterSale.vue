<template>
  <div class="container after-page">
    <h2 class="page-title">我的售后</h2>

    <el-tabs v-model="activeType" @tab-change="load">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="退款" name="1" />
      <el-tab-pane label="发票" name="2" />
      <el-tab-pane label="纠纷" name="3" />
    </el-tabs>

    <div v-if="list.length" class="as-list">
      <div v-for="a in list" :key="a.id" class="as-card">
        <div class="as-head">
          <el-tag :type="typeTag(a.type)" size="small">{{ a.typeDesc }}</el-tag>
          <span class="no">{{ a.afterSaleNo }}</span>
          <el-tag :type="statusTag(a.status)" size="small">{{ a.statusDesc }}</el-tag>
        </div>
        <div class="as-body">
          <div class="row"><span class="label">订单号</span>{{ a.orderNo }}</div>
          <div v-if="a.type !== 2" class="row"><span class="label">退款金额</span><span class="price">¥{{ formatPrice(a.refundAmount) }}</span></div>
          <div class="row"><span class="label">申请原因</span>{{ a.reason || '—' }}</div>
          <div v-if="a.type === 2" class="row">
            <span class="label">发票抬头</span>{{ a.invoiceTitle || '—' }}（{{ a.invoiceType === 1 ? '个人' : '公司' }}）
          </div>
          <div v-if="a.handleRemark" class="row"><span class="label">处理说明</span>{{ a.handleRemark }}</div>
          <div class="row"><span class="label">申请时间</span>{{ (a.createTime || '').replace('T', ' ').slice(0, 16) }}</div>
        </div>
        <div class="as-foot">
          <el-button v-if="a.status === 0" size="small" @click="onCancel(a)">撤销</el-button>
          <el-button v-if="a.type === 1 && a.status === 3" size="small" type="warning" @click="onEscalate(a)">升级纠纷</el-button>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无售后记录" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAfterSaleList, cancelAfterSale, escalateAfterSale } from '@/api'
import { formatPrice } from '@/utils'

const activeType = ref('')
const list = ref([])

const typeTag = (t) => ({ 1: 'danger', 2: 'primary', 3: 'warning' }[t] || 'info')
const statusTag = (s) => ({ 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger', 4: 'info' }[s] || 'info')

const load = async () => {
  const params = { pageNum: 1, pageSize: 50 }
  if (activeType.value !== '') params.type = Number(activeType.value)
  const page = await getAfterSaleList(params)
  list.value = page.records || []
}

const onCancel = async (a) => {
  await ElMessageBox.confirm('确认撤销该售后申请吗？', '提示', { type: 'warning' })
  await cancelAfterSale(a.id)
  ElMessage.success('已撤销')
  load()
}

const onEscalate = async (a) => {
  await ElMessageBox.confirm('确认将该退款升级为纠纷吗？', '提示', { type: 'warning' })
  await escalateAfterSale(a.id)
  ElMessage.success('已升级为纠纷')
  load()
}

onMounted(load)
</script>

<style scoped>
.after-page {
  margin-top: 20px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #333;
}
.as-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.as-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid var(--mall-border);
  overflow: hidden;
}
.as-head {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: #fafafa;
  border-bottom: 1px solid var(--mall-border);
}
.as-head .no {
  flex: 1;
  color: #666;
  font-size: 13px;
}
.as-body {
  padding: 12px 20px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.row {
  color: #555;
  font-size: 13px;
}
.row .label {
  display: inline-block;
  width: 70px;
  color: #999;
}
.row .price {
  font-size: 14px;
}
.as-foot {
  padding: 10px 20px;
  border-top: 1px solid var(--mall-border);
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
