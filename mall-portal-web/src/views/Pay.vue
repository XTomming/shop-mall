<template>
  <div class="pay-page">
    <div class="pay-card" v-if="order">
      <el-result icon="warning" title="订单待支付" sub-title="请完成模拟支付">
        <template #extra>
          <div class="pay-info">
            <div class="row">
              <span>订单编号：</span>
              <b>{{ order.orderNo }}</b>
            </div>
            <div class="row">
              <span>支付金额：</span>
              <b class="price">¥{{ formatPrice(order.payAmount) }}</b>
            </div>
            <el-button type="primary" size="large" :loading="loading" @click="onPay">
              模拟支付
            </el-button>
            <el-button size="large" @click="$router.push('/order/list')">暂不支付</el-button>
          </div>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderDetail, mockPay } from '@/api'
import { formatPrice } from '@/utils'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(false)

onMounted(async () => {
  order.value = await getOrderDetail(route.params.id)
})

const onPay = async () => {
  loading.value = true
  try {
    await mockPay({ orderId: order.value.id, payType: 3 })
    ElMessage.success('支付成功')
    router.push('/order/list')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.pay-page {
  min-height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.pay-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  width: 520px;
  border: 1px solid var(--mall-border);
}
.pay-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
}
.row {
  color: #666;
  font-size: 14px;
}
.row .price {
  font-size: 20px;
}
</style>
