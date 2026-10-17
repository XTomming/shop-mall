<template>
  <div class="container detail-page" v-if="product">
    <!-- 商品主信息 -->
    <div class="main-card">
      <div class="gallery">
        <el-image :src="product.mainImage || PLACEHOLDER_IMG" fit="cover" class="main-img" />
        <div class="thumbs">
          <el-image
            v-for="(img, i) in product.images"
            :key="i"
            :src="img"
            fit="cover"
            class="thumb"
            :class="{ active: currentImg === img }"
            @click="currentImg = img"
          />
        </div>
      </div>

      <div class="info">
        <h1 class="name">{{ product.name }}</h1>
        <p class="subtitle">{{ product.subtitle }}</p>
        <div class="price-box">
          <span class="label">价格</span>
          <span class="price"><i>¥</i>{{ formatPrice(product.price) }}</span>
          <span v-if="product.originalPrice" class="original">¥{{ formatPrice(product.originalPrice) }}</span>
        </div>
        <div class="meta">
          <span>库存：{{ product.stock }}</span>
          <span>销量：{{ product.sales }}</span>
        </div>
        <div class="buy-row">
          <span class="label">数量</span>
          <el-input-number v-model="quantity" :min="1" :max="product.stock || 1" />
        </div>
        <div class="actions">
          <el-button size="large" @click="onAddCart(false)">加入购物车</el-button>
          <el-button size="large" type="primary" @click="onAddCart(true)">立即购买</el-button>
          <el-button size="large" type="warning" @click="onConsult">咨询客服</el-button>
        </div>
      </div>
    </div>

    <!-- 商品详情 -->
    <div class="section-card">
      <h2 class="section-title">商品详情</h2>
      <div class="detail-html" v-html="product.detail || '<p>暂无详情</p>'"></div>
    </div>

    <!-- 商品评价 -->
    <div class="section-card">
      <h2 class="section-title">商品评价（{{ comments.length }}）</h2>
      <div v-if="comments.length" class="comment-list">
        <div v-for="c in comments" :key="c.id" class="comment">
          <div class="comment-head">
            <span class="comment-user">{{ c.nickname || '匿名用户' }}</span>
            <el-rate :model-value="c.rating" disabled size="small" />
            <span class="comment-time">{{ (c.createTime || '').replace('T', ' ').slice(0, 16) }}</span>
          </div>
          <p class="comment-content">{{ c.content || '此用户没有填写评价内容' }}</p>
        </div>
      </div>
      <el-empty v-else description="暂无评价" :image-size="80" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail, getComments, addCart } from '@/api'
import { useCartStore } from '@/stores/cart'
import { formatPrice, PLACEHOLDER_IMG } from '@/utils'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const product = ref(null)
const comments = ref([])
const quantity = ref(1)
const currentImg = ref('')

onMounted(async () => {
  const id = route.params.id
  product.value = await getProductDetail(id)
  currentImg.value = product.value.mainImage
  comments.value = await getComments(id)
})

const onAddCart = async (buyNow) => {
  const token = sessionStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  await addCart({ productId: product.value.id, quantity: quantity.value })
  cartStore.refreshCount()
  if (buyNow) {
    router.push('/cart')
  } else {
    ElMessage.success('已加入购物车')
  }
}

const onConsult = () => {
  const token = sessionStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  router.push({ path: '/chat', query: { productId: product.value.id } })
}
</script>

<style scoped>
.detail-page {
  margin-top: 20px;
}
.main-card {
  display: flex;
  gap: 32px;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  border: 1px solid var(--mall-border);
}
.gallery {
  width: 420px;
}
.main-img {
  width: 420px;
  height: 420px;
  border-radius: 8px;
  background: #f7f7f7;
}
.thumbs {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}
.thumb {
  width: 64px;
  height: 64px;
  border-radius: 6px;
  cursor: pointer;
  border: 2px solid transparent;
}
.thumb.active {
  border-color: var(--mall-primary);
}

.info {
  flex: 1;
}
.name {
  font-size: 22px;
  font-weight: 700;
  color: #333;
}
.subtitle {
  margin-top: 8px;
  color: #999;
  font-size: 14px;
}
.price-box {
  margin-top: 20px;
  background: #fff7f2;
  padding: 16px;
  border-radius: 8px;
  display: flex;
  align-items: baseline;
  gap: 12px;
}
.label {
  font-size: 13px;
  color: #999;
}
.price {
  color: var(--mall-price);
  font-size: 28px;
  font-weight: 700;
}
.price i {
  font-style: normal;
  font-size: 16px;
  margin-right: 2px;
}
.original {
  color: #bbb;
  text-decoration: line-through;
  font-size: 14px;
}
.meta {
  margin-top: 16px;
  color: #666;
  font-size: 13px;
  display: flex;
  gap: 24px;
}
.buy-row {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.actions {
  margin-top: 28px;
  display: flex;
  gap: 16px;
}

.section-card {
  margin-top: 20px;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  border: 1px solid var(--mall-border);
}
.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
  margin-bottom: 16px;
}
.detail-html {
  color: #555;
  line-height: 1.8;
}
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.comment {
  padding-bottom: 16px;
  border-bottom: 1px dashed var(--mall-border);
}
.comment-head {
  display: flex;
  align-items: center;
  gap: 12px;
}
.comment-user {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}
.comment-time {
  margin-left: auto;
  color: #aaa;
  font-size: 12px;
}
.comment-content {
  margin-top: 8px;
  color: #555;
  font-size: 14px;
}
</style>
