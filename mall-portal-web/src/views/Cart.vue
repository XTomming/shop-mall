<template>
  <div class="container cart-page">
    <h2 class="page-title">我的购物车</h2>

    <div v-if="cartList.length" class="cart-table">
      <div class="cart-head">
        <el-checkbox :model-value="allChecked" @change="toggleAll">全选</el-checkbox>
        <span class="col-product">商品</span>
        <span class="col-price">单价</span>
        <span class="col-qty">数量</span>
        <span class="col-subtotal">小计</span>
        <span class="col-op">操作</span>
      </div>

      <div v-for="item in cartList" :key="item.id" class="cart-row">
        <el-checkbox :model-value="item.checked === 1" @change="(v) => toggleChecked(item, v)" />
        <div class="col-product">
          <el-image :src="item.productImage || PLACEHOLDER_IMG" fit="cover" class="thumb" />
          <router-link :to="`/product/detail/${item.productId}`" class="p-name">{{ item.productName }}</router-link>
        </div>
        <span class="col-price price">¥{{ formatPrice(item.price) }}</span>
        <div class="col-qty">
          <el-input-number v-model="item.quantity" :min="1" :max="item.stock || 1" size="small" @change="(v) => changeQty(item, v)" />
        </div>
        <span class="col-subtotal price">¥{{ formatPrice(item.price * item.quantity) }}</span>
        <div class="col-op">
          <el-button link type="danger" @click="onDelete(item)">删除</el-button>
        </div>
      </div>
    </div>
    <el-empty v-else description="购物车是空的，快去逛逛吧">
      <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
    </el-empty>

    <!-- 结算栏 -->
    <div v-if="cartList.length" class="settle-bar">
      <span class="total-label">已选 {{ selected.length }} 件，合计：</span>
      <span class="total-price price">¥{{ formatPrice(totalPrice) }}</span>
      <el-button type="primary" size="large" :disabled="selected.length === 0" @click="onCheckout">
        去结算
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCartList, updateCart, deleteCart } from '@/api'
import { formatPrice, PLACEHOLDER_IMG } from '@/utils'

const router = useRouter()
const cartList = ref([])

const selected = computed(() => cartList.value.filter((i) => i.checked === 1))
const totalPrice = computed(() =>
  selected.value.reduce((sum, i) => sum + i.price * i.quantity, 0)
)
const allChecked = computed(
  () => cartList.value.length > 0 && cartList.value.every((i) => i.checked === 1)
)

const load = async () => {
  cartList.value = await getCartList()
}

const toggleChecked = async (item, val) => {
  item.checked = val ? 1 : 0
  await updateCart(item.id, { checked: item.checked })
}

const toggleAll = async (val) => {
  for (const item of cartList.value) {
    item.checked = val ? 1 : 0
    await updateCart(item.id, { checked: item.checked })
  }
}

const changeQty = async (item, val) => {
  await updateCart(item.id, { quantity: val })
}

const onDelete = async (item) => {
  await ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' })
  await deleteCart(item.id)
  ElMessage.success('已删除')
  load()
}

const onCheckout = () => {
  const ids = selected.value.map((i) => i.id)
  router.push({ path: '/order/confirm', query: { cartIds: ids.join(',') } })
}

onMounted(load)
</script>

<style scoped>
.cart-page {
  margin-top: 20px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #333;
}
.cart-table {
  background: #fff;
  border-radius: 8px;
  border: 1px solid var(--mall-border);
  overflow: hidden;
}
.cart-head,
.cart-row {
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 16px;
}
.cart-head {
  height: 48px;
  background: #fafafa;
  color: #666;
  font-size: 13px;
  border-bottom: 1px solid var(--mall-border);
}
.cart-row {
  min-height: 100px;
  border-bottom: 1px solid var(--mall-border);
}
.cart-row:last-child {
  border-bottom: none;
}
.col-product {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}
.thumb {
  width: 72px;
  height: 72px;
  border-radius: 6px;
  flex-shrink: 0;
}
.p-name {
  color: #333;
  font-size: 14px;
}
.p-name:hover {
  color: var(--mall-primary);
}
.col-price,
.col-subtotal {
  width: 110px;
  text-align: center;
  font-size: 14px;
}
.col-qty {
  width: 140px;
  display: flex;
  justify-content: center;
}
.col-op {
  width: 60px;
  text-align: center;
}

.settle-bar {
  margin-top: 20px;
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  border: 1px solid var(--mall-border);
}
.total-label {
  color: #666;
  font-size: 14px;
}
.total-price {
  font-size: 22px;
}
</style>
