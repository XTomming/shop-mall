<template>
  <router-link :to="`/product/detail/${product.id}`" class="card card-hover">
    <div class="img-box">
      <el-image :src="product.mainImage || PLACEHOLDER_IMG" fit="cover" />
    </div>
    <div class="info">
      <div class="name" :title="product.name">{{ product.name }}</div>
      <div class="subtitle" :title="product.subtitle">{{ product.subtitle || ' ' }}</div>
      <div class="bottom">
        <span class="price"><i>¥</i>{{ formatPrice(product.price) }}</span>
        <span class="sales">已售 {{ product.sales || 0 }}</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { formatPrice, PLACEHOLDER_IMG } from '@/utils'

defineProps({
  product: { type: Object, required: true }
})
</script>

<style scoped>
.card {
  display: block;
  background: var(--mall-card);
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--mall-border);
}
.img-box {
  width: 100%;
  aspect-ratio: 1;
  background: #f7f7f7;
}
.img-box :deep(.el-image) {
  width: 100%;
  height: 100%;
}
.info {
  padding: 12px;
}
.name {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
  height: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.bottom {
  margin-top: 10px;
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}
.price {
  color: var(--mall-price);
  font-weight: 700;
  font-size: 18px;
}
.price i {
  font-style: normal;
  font-size: 12px;
  margin-right: 1px;
}
.sales {
  font-size: 12px;
  color: #aaa;
}
</style>
