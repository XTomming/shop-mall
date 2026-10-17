<template>
  <div>
    <!-- 轮播 + 公告 -->
    <div class="container banner-wrap">
      <div class="banner">
        <el-carousel v-if="home.banners?.length" height="360px" :interval="4000">
          <el-carousel-item v-for="b in home.banners" :key="b.id">
            <el-image :src="b.image || PLACEHOLDER_IMG" fit="cover" class="banner-img" />
          </el-carousel-item>
        </el-carousel>
        <div v-else class="banner banner-empty">暂无轮播图</div>
      </div>

      <div class="notices">
        <div class="notices-title">
          <el-icon><Bell /></el-icon>
          <span>商城公告</span>
        </div>
        <ul>
          <li v-for="n in home.notices" :key="n.id" :title="n.content">
            {{ n.title }}
          </li>
          <li v-if="!home.notices?.length" class="empty">暂无公告</li>
        </ul>
      </div>
    </div>

    <!-- 热销商品 -->
    <section class="container section">
      <h2 class="section-title">热销商品</h2>
      <div class="product-grid">
        <ProductCard v-for="p in hotProducts" :key="p.id" :product="p" />
      </div>
      <div class="more-wrap" v-if="hotProducts.length > 0">
        <el-button v-if="hotProducts.length < hotTotal" :loading="loadingMore" @click="loadMore">
          显示更多
        </el-button>
        <span v-else class="no-more">已经到底啦～</span>
      </div>
      <el-empty v-if="hotProducts.length === 0" description="暂无商品" />
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { getHome, getProductPage } from '@/api'
import ProductCard from '@/components/ProductCard.vue'
import { PLACEHOLDER_IMG } from '@/utils'

const home = ref({})

// 热销商品分页加载
const hotProducts = ref([])
const hotTotal = ref(0)
const hotPage = ref(1)
const hotPageSize = 20
const loadingMore = ref(false)

const loadHot = async (page) => {
  const res = await getProductPage({ pageNum: page, pageSize: hotPageSize, sort: 'sales_desc' })
  hotProducts.value = hotProducts.value.concat(res.records || [])
  hotTotal.value = Number(res.total || 0)
}

const loadMore = async () => {
  loadingMore.value = true
  try {
    hotPage.value += 1
    await loadHot(hotPage.value)
  } finally {
    loadingMore.value = false
  }
}

onMounted(async () => {
  home.value = await getHome()
  await loadHot(1)
})
</script>

<style scoped>
.banner-wrap {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}
.banner {
  flex: 1;
  border-radius: 8px;
  overflow: hidden;
}
.banner-img {
  width: 100%;
  height: 100%;
}
.banner-empty {
  height: 360px;
  background: #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #aaa;
}
.notices {
  width: 320px;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid var(--mall-border);
}
.notices-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 700;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--mall-border);
}
.notices ul li {
  padding: 10px 0;
  font-size: 13px;
  color: #555;
  border-bottom: 1px dashed var(--mall-border);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: default;
}
.notices .empty {
  color: #aaa;
  text-align: center;
}

.section {
  margin-top: 32px;
}
.section-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #333;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}
.more-wrap {
  margin-top: 24px;
  text-align: center;
}
.more-wrap .el-button {
  min-width: 180px;
}
.no-more {
  color: #bbb;
  font-size: 13px;
}
</style>
