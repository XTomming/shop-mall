<template>
  <div class="container seckill-page">
    <div v-for="activity in activities" :key="activity.id" class="activity">
      <div class="activity-head">
        <h2 class="activity-name">{{ activity.name }}</h2>
        <div class="countdown" :class="statusClass(activity)">
          {{ statusText(activity) }}
        </div>
      </div>
      <div class="item-grid">
        <div v-for="item in activity.items" :key="item.id" class="item card-hover">
          <el-image :src="item.productImage || PLACEHOLDER_IMG" fit="cover" class="item-img" />
          <div class="item-info">
            <div class="item-name" :title="item.productName">{{ item.productName }}</div>
            <div class="price-row">
              <span class="seckill-price"><i>¥</i>{{ formatPrice(item.seckillPrice) }}</span>
              <span class="original-price">¥{{ formatPrice(item.originalPrice) }}</span>
            </div>
            <div class="stock">剩余 {{ item.remainingStock }} 件</div>
            <el-button type="danger" size="small" :disabled="getStatus(activity) !== 1" @click="onBuy(item)">
              {{ buyText(activity) }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-if="!activities.length" description="暂无秒杀活动" />
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSeckillList, seckillBuy } from '@/api'
import { formatPrice, PLACEHOLDER_IMG } from '@/utils'

const router = useRouter()
const activities = ref([])
const now = ref(Date.now())
let timer = null

// 活动状态：0 未开始，1 进行中，2 已结束
const getStatus = (activity) => {
  const start = new Date(activity.startTime).getTime()
  const end = new Date(activity.endTime).getTime()
  if (now.value < start) return 0
  if (now.value > end) return 2
  return 1
}

const statusClass = (activity) => ['pending', 'active', 'ended'][getStatus(activity)]

const statusText = (activity) => {
  const s = getStatus(activity)
  if (s === 1) return '距结束 ' + formatCountdown(new Date(activity.endTime).getTime() - now.value)
  if (s === 0) return '距开始 ' + formatCountdown(new Date(activity.startTime).getTime() - now.value)
  return '已结束'
}

const buyText = (activity) => {
  const s = getStatus(activity)
  if (s === 1) return '立即秒杀'
  if (s === 0) return '未开始'
  return '已结束'
}

const formatCountdown = (ms) => {
  if (ms < 0) ms = 0
  const s = Math.floor(ms / 1000)
  const h = String(Math.floor(s / 3600)).padStart(2, '0')
  const m = String(Math.floor((s % 3600) / 60)).padStart(2, '0')
  const sec = String(s % 60).padStart(2, '0')
  return `${h}:${m}:${sec}`
}

const onBuy = async (item) => {
  const token = sessionStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push({ name: 'login', query: { redirect: '/seckill' } })
    return
  }
  await seckillBuy(item.id)
  ElMessage.success('抢购成功，订单生成中，请到「我的订单」查看')
}

onMounted(async () => {
  activities.value = await getSeckillList()
  timer = setInterval(() => {
    now.value = Date.now()
  }, 1000)
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.seckill-page {
  margin-top: 20px;
}
.activity {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid var(--mall-border);
}
.activity-head {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}
.activity-name {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}
.countdown {
  font-size: 13px;
  padding: 4px 12px;
  border-radius: 14px;
}
.countdown.active {
  background: #fff0ec;
  color: var(--mall-primary);
}
.countdown.pending {
  background: #f0f0f0;
  color: #888;
}
.countdown.ended {
  background: #f0f0f0;
  color: #aaa;
}
.item-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}
.item {
  border: 1px solid var(--mall-border);
  border-radius: 8px;
  overflow: hidden;
}
.item-img {
  width: 100%;
  aspect-ratio: 1;
  background: #f7f7f7;
}
.item-info {
  padding: 12px;
}
.item-name {
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.price-row {
  margin-top: 8px;
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.seckill-price {
  color: var(--mall-price);
  font-weight: 700;
  font-size: 18px;
}
.seckill-price i {
  font-style: normal;
  font-size: 12px;
}
.original-price {
  color: #bbb;
  text-decoration: line-through;
  font-size: 12px;
}
.stock {
  margin: 6px 0 10px;
  font-size: 12px;
  color: #999;
}
</style>
