<template>
  <div class="container list-page">
    <!-- 排序栏 -->
    <div class="sort-bar">
      <span
        v-for="s in sorts"
        :key="s.key"
        class="sort-item"
        :class="{ active: sort === s.key }"
        @click="changeSort(s.key)"
      >
        {{ s.label }}
      </span>
      <span v-if="keyword" class="keyword-tip">搜索结果：{{ keyword }}</span>
    </div>

    <!-- 商品网格 -->
    <div v-if="products.length" class="product-grid">
      <ProductCard v-for="p in products" :key="p.id" :product="p" />
    </div>
    <el-empty v-else description="暂无商品" />

    <!-- 分页 -->
    <div class="pager">
      <el-pagination
        background
        layout="prev, pager, next, total"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProductPage } from '@/api'
import ProductCard from '@/components/ProductCard.vue'

const route = useRoute()

const sorts = [
  { key: '', label: '综合' },
  { key: 'sales_desc', label: '销量' },
  { key: 'price_asc', label: '价格 ↑' },
  { key: 'price_desc', label: '价格 ↓' }
]

const products = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const sort = ref('')
const keyword = ref('')
const categoryId = ref(null)

const load = async () => {
  const params = { pageNum: pageNum.value, pageSize: pageSize.value }
  if (keyword.value) params.keyword = keyword.value
  if (categoryId.value) params.categoryId = categoryId.value
  if (sort.value) params.sort = sort.value
  const page = await getProductPage(params)
  products.value = page.records || []
  total.value = Number(page.total || 0)
}

const changeSort = (key) => {
  sort.value = key
  pageNum.value = 1
  load()
}

const onPageChange = (p) => {
  pageNum.value = p
  load()
}

watch(
  () => [route.query.keyword, route.query.categoryId],
  () => {
    keyword.value = route.query.keyword || ''
    categoryId.value = route.query.categoryId || null
    pageNum.value = 1
    load()
  },
  { immediate: true }
)
</script>

<style scoped>
.list-page {
  margin-top: 20px;
}
.sort-bar {
  background: #fff;
  border-radius: 8px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 28px;
  height: 48px;
  margin-bottom: 20px;
  border: 1px solid var(--mall-border);
}
.sort-item {
  cursor: pointer;
  font-size: 14px;
  color: #555;
  height: 48px;
  line-height: 48px;
}
.sort-item:hover,
.sort-item.active {
  color: var(--mall-primary);
  font-weight: 600;
}
.keyword-tip {
  margin-left: auto;
  font-size: 13px;
  color: #999;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}
.pager {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
