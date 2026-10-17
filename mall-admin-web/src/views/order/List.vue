<template>
  <div class="table-page">
    <div class="toolbar">
      <el-input v-model="query.orderNo" placeholder="订单编号" clearable style="width: 220px" @keyup.enter="search" />
      <el-select v-model="query.status" placeholder="订单状态" clearable style="width: 140px">
        <el-option label="待付款" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="待收货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已关闭" :value="4" />
      </el-select>
      <el-button type="primary" @click="search">搜索</el-button>
    </div>

    <div class="panel-card">
      <el-table :data="list" style="width: 100%">
        <el-table-column prop="orderNo" label="订单编号" min-width="180" />
        <el-table-column prop="memberId" label="会员ID" width="80" />
        <el-table-column label="实付金额" width="110">
          <template #default="{ row }">¥{{ formatPrice(row.payAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.statusDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="收货人" width="110">
          <template #default="{ row }">{{ row.receiverName }}</template>
        </el-table-column>
        <el-table-column label="下单时间" width="170">
          <template #default="{ row }">{{ (row.createTime || '').replace('T', ' ').slice(0, 16) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/order/detail/${row.id}`)">详情</el-button>
            <el-button v-if="row.status === 1" link type="success" @click="onDelivery(row)">发货</el-button>
            <el-button v-if="row.status === 0" link type="danger" @click="onClose(row)">关闭</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="query.pageSize"
          :current-page="query.pageNum"
          @current-change="onPageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPage, deliveryOrder, closeOrder } from '@/api'
import { formatPrice } from '@/utils'

const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, orderNo: '', status: null })

const statusType = (s) => ['warning', 'primary', 'info', 'success', 'danger'][s] || 'info'

const load = async () => {
  const params = { pageNum: query.pageNum, pageSize: query.pageSize }
  if (query.orderNo) params.orderNo = query.orderNo
  if (query.status !== null && query.status !== '') params.status = query.status
  const page = await getOrderPage(params)
  list.value = page.records || []
  total.value = Number(page.total || 0)
}

const search = () => {
  query.pageNum = 1
  load()
}

const onPageChange = (p) => {
  query.pageNum = p
  load()
}

const onDelivery = async (row) => {
  await ElMessageBox.confirm('确认发货吗？', '提示', { type: 'warning' })
  await deliveryOrder(row.id)
  ElMessage.success('已发货')
  load()
}

const onClose = async (row) => {
  await ElMessageBox.confirm('确认关闭该订单吗？', '提示', { type: 'warning' })
  await closeOrder(row.id)
  ElMessage.success('已关闭')
  load()
}

onMounted(load)
</script>

<style scoped>
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
