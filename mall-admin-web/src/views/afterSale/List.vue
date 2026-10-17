<template>
  <div class="table-page">
    <!-- 统计卡片 -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-label">售后总数</div>
        <div class="stat-value">{{ stat.total ?? 0 }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">待处理</div>
        <div class="stat-value">{{ stat.pending ?? 0 }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">处理中</div>
        <div class="stat-value">{{ stat.processing ?? 0 }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">累计退款（¥）</div>
        <div class="stat-value accent">{{ formatPrice(stat.refundTotal) }}</div>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="toolbar">
      <el-select v-model="query.type" placeholder="类型" clearable style="width: 140px">
        <el-option label="退款" :value="1" />
        <el-option label="发票" :value="2" />
        <el-option label="纠纷" :value="3" />
      </el-select>
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
        <el-option label="待处理" :value="0" />
        <el-option label="处理中" :value="1" />
        <el-option label="已完成" :value="2" />
        <el-option label="已拒绝" :value="3" />
        <el-option label="已关闭" :value="4" />
      </el-select>
      <el-button type="primary" @click="search">搜索</el-button>
    </div>

    <!-- 列表 -->
    <div class="panel-card">
      <el-table :data="list" style="width: 100%">
        <el-table-column prop="afterSaleNo" label="售后单号" min-width="180" />
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="typeTag(row.type)">{{ row.typeDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="statusTag(row.status)">{{ row.statusDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="memberNickname" label="会员" width="120" />
        <el-table-column prop="orderNo" label="订单号" min-width="170" />
        <el-table-column label="退款金额" width="110">
          <template #default="{ row }">{{ row.type !== 2 ? '¥' + formatPrice(row.refundAmount) : '—' }}</template>
        </el-table-column>
        <el-table-column prop="reason" label="申请原因" min-width="160" show-overflow-tooltip />
        <el-table-column label="申请时间" width="160">
          <template #default="{ row }">{{ (row.createTime || '').replace('T', ' ').slice(0, 16) }}</template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="query.pageSize"
          :current-page="query.pageNum"
          @current-change="onPageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAfterSalePage, getAfterSaleStat } from '@/api'

const list = ref([])
const total = ref(0)
const stat = ref({})
const query = reactive({ pageNum: 1, pageSize: 10, type: null, status: null })

const formatPrice = (v) => Number(v ?? 0).toFixed(2)
const typeTag = (t) => ({ 1: 'danger', 2: 'primary', 3: 'warning' }[t] || 'info')
const statusTag = (s) => ({ 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger', 4: 'info' }[s] || 'info')

const load = async () => {
  const params = { pageNum: query.pageNum, pageSize: query.pageSize }
  if (query.type) params.type = query.type
  if (query.status !== null && query.status !== '') params.status = query.status
  const page = await getAfterSalePage(params)
  list.value = page.records || []
  total.value = Number(page.total || 0)
}

const loadStat = async () => {
  stat.value = await getAfterSaleStat()
}

const search = () => {
  query.pageNum = 1
  load()
}

const onPageChange = (p) => {
  query.pageNum = p
  load()
}

onMounted(() => {
  load()
  loadStat()
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
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
