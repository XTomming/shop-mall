<template>
  <div class="container confirm-page">
    <h2 class="page-title">确认订单</h2>

    <!-- 收货地址 -->
    <div class="card">
      <div class="card-title">收货地址</div>
      <div v-if="addressList.length" class="addr-list">
        <div
          v-for="a in addressList"
          :key="a.id"
          class="addr-item"
          :class="{ active: selectedAddressId === a.id }"
          @click="selectedAddressId = a.id"
        >
          <div class="addr-top">
            <span class="receiver">{{ a.receiverName }}</span>
            <span class="phone">{{ a.phone }}</span>
            <el-tag v-if="a.isDefault === 1" size="small" type="warning">默认</el-tag>
          </div>
          <div class="addr-detail">{{ a.province }}{{ a.city }}{{ a.district }}{{ a.detailAddress }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无收货地址" :image-size="60">
        <el-button size="small" type="primary" @click="$router.push('/user/address')">去添加地址</el-button>
      </el-empty>
    </div>

    <!-- 商品清单 -->
    <div class="card">
      <div class="card-title">商品清单</div>
      <div class="item-row" v-for="item in items" :key="item.id">
        <el-image :src="item.productImage || PLACEHOLDER_IMG" fit="cover" class="thumb" />
        <span class="i-name">{{ item.productName }}</span>
        <span class="i-price">¥{{ formatPrice(item.price) }}</span>
        <span class="i-qty">×{{ item.quantity }}</span>
        <span class="i-subtotal price">¥{{ formatPrice(item.price * item.quantity) }}</span>
      </div>
      <div class="total-line">
        <span>商品合计：</span>
        <span class="price">¥{{ formatPrice(totalPrice) }}</span>
      </div>
    </div>

    <!-- 提交 -->
    <div class="submit-bar">
      <el-button type="primary" size="large" :disabled="!selectedAddressId" :loading="loading" @click="onSubmit">
        提交订单
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCartList, getAddressList, submitOrder } from '@/api'
import { useCartStore } from '@/stores/cart'
import { formatPrice, PLACEHOLDER_IMG } from '@/utils'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const cartIds = ref([])
const items = ref([])
const addressList = ref([])
const selectedAddressId = ref(null)
const loading = ref(false)

const totalPrice = computed(() =>
  items.value.reduce((sum, i) => sum + i.price * i.quantity, 0)
)

onMounted(async () => {
  cartIds.value = (route.query.cartIds || '').split(',').map(Number).filter(Boolean)
  const cartList = await getCartList()
  items.value = cartList.filter((i) => cartIds.value.includes(i.id))

  addressList.value = await getAddressList()
  const def = addressList.value.find((a) => a.isDefault === 1)
  selectedAddressId.value = (def || addressList.value[0])?.id
})

const onSubmit = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  loading.value = true
  try {
    const order = await submitOrder({ addressId: selectedAddressId.value, cartIds: cartIds.value })
    cartStore.refreshCount()
    ElMessage.success('下单成功')
    router.push(`/order/pay/${order.id}`)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.confirm-page {
  margin-top: 20px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #333;
}
.card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid var(--mall-border);
}
.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #333;
  margin-bottom: 16px;
}
.addr-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.addr-item {
  border: 1px solid var(--mall-border);
  border-radius: 8px;
  padding: 14px;
  cursor: pointer;
  transition: border-color 0.2s;
}
.addr-item.active {
  border-color: var(--mall-primary);
  background: #fff7f2;
}
.addr-top {
  display: flex;
  align-items: center;
  gap: 8px;
}
.receiver {
  font-weight: 600;
  color: #333;
}
.phone {
  color: #666;
  font-size: 13px;
}
.addr-detail {
  margin-top: 8px;
  color: #666;
  font-size: 13px;
  line-height: 1.5;
}

.item-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px dashed var(--mall-border);
}
.thumb {
  width: 64px;
  height: 64px;
  border-radius: 6px;
}
.i-name {
  flex: 1;
  color: #333;
  font-size: 14px;
}
.i-price {
  color: #555;
  font-size: 14px;
}
.i-qty {
  color: #666;
  font-size: 13px;
}
.i-subtotal {
  font-size: 14px;
}
.total-line {
  margin-top: 16px;
  text-align: right;
  color: #666;
}
.total-line .price {
  font-size: 20px;
  margin-left: 8px;
}
.submit-bar {
  display: flex;
  justify-content: flex-end;
}
</style>
