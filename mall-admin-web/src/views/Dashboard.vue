<template>
  <div class="table-page">
    <!-- 统计卡片 -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-label">会员总数</div>
        <div class="stat-value">{{ data.memberCount ?? 0 }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">商品总数</div>
        <div class="stat-value">{{ data.productCount ?? 0 }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">订单总数</div>
        <div class="stat-value">{{ data.orderCount ?? 0 }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">累计销售额（¥）</div>
        <div class="stat-value accent">{{ formatPrice(data.totalSales) }}</div>
      </div>
    </div>

    <!-- 图表 -->
    <div class="charts">
      <div class="panel-card chart-box">
        <h3 class="chart-title">近 7 天订单量</h3>
        <div ref="orderChartRef" class="chart"></div>
      </div>
      <div class="panel-card chart-box">
        <h3 class="chart-title">近 7 天销售额</h3>
        <div ref="salesChartRef" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { getDashboard } from '@/api'

const data = ref({})
const orderChartRef = ref()
const salesChartRef = ref()
let orderChart = null
let salesChart = null

const formatPrice = (v) => Number(v ?? 0).toFixed(2)

const initCharts = () => {
  const axisStyle = {
    axisLabel: { color: '#94a3b8' },
    axisLine: { lineStyle: { color: '#334155' } },
    splitLine: { lineStyle: { color: '#1f2937' } }
  }

  orderChart = echarts.init(orderChartRef.value)
  orderChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: data.value.days || [], ...axisStyle },
    yAxis: { type: 'value', ...axisStyle },
    series: [
      {
        type: 'bar',
        data: data.value.orderCountPerDay || [],
        barWidth: 20,
        itemStyle: { color: '#ff5000', borderRadius: [4, 4, 0, 0] }
      }
    ]
  })

  salesChart = echarts.init(salesChartRef.value)
  salesChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: data.value.days || [], ...axisStyle },
    yAxis: { type: 'value', ...axisStyle },
    series: [
      {
        type: 'line',
        data: data.value.salesPerDay || [],
        smooth: true,
        areaStyle: { color: 'rgba(255,80,0,0.2)' },
        lineStyle: { color: '#ff5000' },
        itemStyle: { color: '#ff5000' }
      }
    ]
  })
}

const resize = () => {
  orderChart?.resize()
  salesChart?.resize()
}

onMounted(async () => {
  data.value = await getDashboard()
  initCharts()
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  orderChart?.dispose()
  salesChart?.dispose()
})
</script>

<style scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
.stat-card {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 10px;
  padding: 20px;
}
.stat-label {
  color: #94a3b8;
  font-size: 13px;
}
.stat-value {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 700;
  color: #f1f5f9;
}
.stat-value.accent {
  color: #ff5000;
}
.charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #e2e8f0;
  margin-bottom: 12px;
}
.chart {
  height: 320px;
}
</style>
