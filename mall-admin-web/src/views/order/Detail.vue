<template>
  <div class="table-page">
    <div class="toolbar">
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div class="panel-card" v-if="order">
      <h3 class="title">订单信息</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag size="small">{{ order.statusDesc }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ formatPrice(order.payAmount) }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ order.payType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ order.receiverName }}（{{ order.receiverPhone }}）</el-descriptions-item>
        <el-descriptions-item label="收货地址">{{ order.receiverAddress }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ (order.createTime || '').replace('T', ' ').slice(0, 19) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ order.payTime ? order.payTime.replace('T', ' ').slice(0, 19) : '-' }}</el-descriptions-item>
      </el-descriptions>

      <h3 class="title" style="margin-top: 24px">商品明细</h3>
      <el-table :data="order.items" style="width: 100%">
        <el-table-column label="商品图" width="80">
          <template #default="{ row }">
            <el-image :src="row.productImage || PLACEHOLDER_IMG" fit="cover" style="width: 48px; height: 48px; border-radius: 6px" />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" min-width="180" />
        <el-table-column label="单价" width="110">
          <template #default="{ row }">¥{{ formatPrice(row.price) }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="小计" width="120">
          <template #default="{ row }">¥{{ formatPrice(row.totalPrice) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderDetail } from '@/api'
import { formatPrice, PLACEHOLDER_IMG } from '@/utils'

const route = useRoute()
const order = ref(null)

onMounted(async () => {
  order.value = await getOrderDetail(route.params.id)
})
</script>

<style scoped>
.title {
  font-size: 15px;
  font-weight: 700;
  color: #e2e8f0;
  margin-bottom: 16px;
}
</style>
