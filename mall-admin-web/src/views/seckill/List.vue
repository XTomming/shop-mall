<template>
  <div class="table-page">
    <el-tabs v-model="activeTab">
      <!-- 场次管理 -->
      <el-tab-pane label="场次管理" name="activity">
        <div class="toolbar">
          <div class="spacer"></div>
          <el-button type="primary" @click="openActivityAdd">新增场次</el-button>
        </div>
        <div class="panel-card">
          <el-table :data="activities" style="width: 100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="场次名称" min-width="140" />
            <el-table-column label="开始时间" width="170">
              <template #default="{ row }">{{ fmtTime(row.startTime) }}</template>
            </el-table-column>
            <el-table-column label="结束时间" width="170">
              <template #default="{ row }">{{ fmtTime(row.endTime) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openActivityEdit(row)">编辑</el-button>
                <el-button link type="danger" @click="onActivityDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <!-- 秒杀商品 -->
      <el-tab-pane label="秒杀商品" name="item">
        <div class="toolbar">
          <el-select v-model="itemQuery.activityId" placeholder="全部场次" clearable style="width: 200px" @change="loadItems">
            <el-option v-for="a in activities" :key="a.id" :label="a.name" :value="a.id" />
          </el-select>
          <div class="spacer"></div>
          <el-button type="primary" @click="openBatchAdd">新增秒杀商品</el-button>
        </div>
        <div class="panel-card">
          <el-table :data="items" style="width: 100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="productName" label="商品" min-width="150" show-overflow-tooltip />
            <el-table-column prop="activityName" label="场次" width="130" />
            <el-table-column label="秒杀价" width="100">
              <template #default="{ row }">¥{{ formatPrice(row.seckillPrice) }}</template>
            </el-table-column>
            <el-table-column prop="seckillStock" label="秒杀库存" width="90" />
            <el-table-column prop="limitPerUser" label="限购" width="70" />
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openItemEdit(row)">编辑</el-button>
                <el-button link type="danger" @click="onItemDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pager">
            <el-pagination
              background
              layout="prev, pager, next, total"
              :total="itemTotal"
              :page-size="itemQuery.pageSize"
              :current-page="itemQuery.pageNum"
              @current-change="onItemPageChange"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 场次弹窗 -->
    <el-dialog v-model="activityVisible" :title="activityEditingId ? '编辑场次' : '新增场次'" width="480px">
      <el-form ref="activityFormRef" :model="activityForm" :rules="activityRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="activityForm.name" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="activityForm.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="activityForm.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择结束时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="activityForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="activityVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="onActivitySubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 批量新增秒杀商品弹窗 -->
    <el-dialog v-model="batchVisible" title="新增秒杀商品（可多选）" width="640px">
      <el-form label-width="80px">
        <el-form-item label="场次" required>
          <el-select v-model="batchForm.activityId" placeholder="选择场次" style="width: 100%">
            <el-option v-for="a in activities" :key="a.id" :label="a.name" :value="a.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择商品" required>
          <el-select v-model="selectedProductIds" multiple filterable placeholder="多选商品" style="width: 100%" @change="onProductsChange">
            <el-option v-for="p in products" :key="p.id" :label="`${p.name}（¥${p.price}）`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="秒杀设置" v-if="selectedItems.length">
          <div class="batch-items">
            <div v-for="it in selectedItems" :key="it.productId" class="batch-item">
              <div class="bi-name">
                {{ it.productName }}
                <span class="bi-original">原价 ¥{{ formatPrice(it.originalPrice) }}</span>
              </div>
              <div class="bi-controls">
                <span class="bi-label">秒杀价</span>
                <el-input-number v-model="it.seckillPrice" :min="0" :precision="2" size="small" style="width: 120px" />
                <span class="bi-label">库存</span>
                <el-input-number v-model="it.seckillStock" :min="0" size="small" style="width: 110px" />
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="onBatchSubmit">批量保存</el-button>
      </template>
    </el-dialog>

    <!-- 编辑秒杀商品弹窗 -->
    <el-dialog v-model="itemVisible" title="编辑秒杀商品" width="480px">
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="80px">
        <el-form-item label="场次" prop="activityId">
          <el-select v-model="itemForm.activityId" placeholder="选择场次" style="width: 100%">
            <el-option v-for="a in activities" :key="a.id" :label="a.name" :value="a.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品" prop="productId">
          <el-select v-model="itemForm.productId" filterable placeholder="搜索选择商品" style="width: 100%">
            <el-option v-for="p in products" :key="p.id" :label="`${p.name}（¥${p.price}）`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="秒杀价" prop="seckillPrice">
          <el-input-number v-model="itemForm.seckillPrice" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="秒杀库存" prop="seckillStock">
          <el-input-number v-model="itemForm.seckillStock" :min="0" style="width: 200px" />
        </el-form-item>
        <el-form-item label="每人限购">
          <el-input-number v-model="itemForm.limitPerUser" :min="1" style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="itemForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="onItemSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getSeckillActivityPage, addSeckillActivity, updateSeckillActivity, deleteSeckillActivity,
  getSeckillItemPage, addSeckillItemBatch, updateSeckillItem, deleteSeckillItem,
  getProductPage
} from '@/api'
import { formatPrice } from '@/utils'

const activeTab = ref('activity')
const loading = ref(false)

// 场次
const activities = ref([])
const activityVisible = ref(false)
const activityEditingId = ref(null)
const activityFormRef = ref()
const activityForm = reactive({ name: '', startTime: '', endTime: '', status: 1 })
const activityRules = {
  name: [{ required: true, message: '请输入场次名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

// 秒杀商品
const items = ref([])
const itemTotal = ref(0)
const itemQuery = reactive({ pageNum: 1, pageSize: 10, activityId: null })
const products = ref([])

// 批量新增
const batchVisible = ref(false)
const batchForm = reactive({ activityId: null })
const selectedProductIds = ref([])
const selectedItems = ref([])

// 单个编辑
const itemVisible = ref(false)
const itemEditingId = ref(null)
const itemFormRef = ref()
const itemForm = reactive({ activityId: null, productId: null, seckillPrice: 0, seckillStock: 0, limitPerUser: 1, status: 1 })
const itemRules = {
  activityId: [{ required: true, message: '请选择场次', trigger: 'change' }],
  productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  seckillPrice: [{ required: true, message: '请输入秒杀价', trigger: 'blur' }],
  seckillStock: [{ required: true, message: '请输入秒杀库存', trigger: 'blur' }]
}

const fmtTime = (t) => (t || '').replace('T', ' ').slice(0, 16)

const loadActivities = async () => {
  const page = await getSeckillActivityPage({ pageNum: 1, pageSize: 100 })
  activities.value = page.records || []
}

const loadItems = async () => {
  const params = { pageNum: itemQuery.pageNum, pageSize: itemQuery.pageSize }
  if (itemQuery.activityId) params.activityId = itemQuery.activityId
  const page = await getSeckillItemPage(params)
  items.value = page.records || []
  itemTotal.value = Number(page.total || 0)
}

const onItemPageChange = (p) => {
  itemQuery.pageNum = p
  loadItems()
}

// 场次 CRUD
const openActivityAdd = () => {
  activityEditingId.value = null
  Object.assign(activityForm, { name: '', startTime: '', endTime: '', status: 1 })
  activityVisible.value = true
}

const openActivityEdit = (row) => {
  activityEditingId.value = row.id
  Object.assign(activityForm, { name: row.name, startTime: row.startTime, endTime: row.endTime, status: row.status })
  activityVisible.value = true
}

const onActivitySubmit = () => {
  activityFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      if (activityEditingId.value) {
        await updateSeckillActivity(activityEditingId.value, { ...activityForm })
      } else {
        await addSeckillActivity({ ...activityForm })
      }
      ElMessage.success('保存成功')
      activityVisible.value = false
      loadActivities()
    } finally {
      loading.value = false
    }
  })
}

const onActivityDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除场次「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteSeckillActivity(row.id)
  ElMessage.success('已删除')
  loadActivities()
}

// 批量新增
const openBatchAdd = () => {
  batchForm.activityId = itemQuery.activityId || null
  selectedProductIds.value = []
  selectedItems.value = []
  batchVisible.value = true
}

const onProductsChange = (ids) => {
  selectedItems.value = selectedItems.value.filter((it) => ids.includes(it.productId))
  ids.forEach((id) => {
    if (!selectedItems.value.find((it) => it.productId === id)) {
      const p = products.value.find((x) => x.id === id)
      selectedItems.value.push({
        productId: id,
        productName: p?.name || '',
        originalPrice: Number(p?.price || 0),
        seckillPrice: p ? Math.floor(Number(p.price) * 0.5) : 0,
        seckillStock: 100,
        limitPerUser: 1
      })
    }
  })
}

const onBatchSubmit = async () => {
  if (!batchForm.activityId) {
    ElMessage.warning('请选择场次')
    return
  }
  if (!selectedItems.value.length) {
    ElMessage.warning('请选择商品')
    return
  }
  loading.value = true
  try {
    await addSeckillItemBatch({
      activityId: batchForm.activityId,
      items: selectedItems.value.map((it) => ({
        productId: it.productId,
        seckillPrice: it.seckillPrice,
        seckillStock: it.seckillStock,
        limitPerUser: it.limitPerUser
      }))
    })
    ElMessage.success('批量新增成功')
    batchVisible.value = false
    itemQuery.activityId = batchForm.activityId
    loadItems()
  } finally {
    loading.value = false
  }
}

// 单个编辑
const openItemEdit = (row) => {
  itemEditingId.value = row.id
  Object.assign(itemForm, {
    activityId: row.activityId,
    productId: row.productId,
    seckillPrice: Number(row.seckillPrice),
    seckillStock: row.seckillStock,
    limitPerUser: row.limitPerUser,
    status: row.status ?? 1
  })
  itemVisible.value = true
}

const onItemSubmit = () => {
  itemFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await updateSeckillItem(itemEditingId.value, { ...itemForm })
      ElMessage.success('保存成功')
      itemVisible.value = false
      loadItems()
    } finally {
      loading.value = false
    }
  })
}

const onItemDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该秒杀商品吗？', '提示', { type: 'warning' })
  await deleteSeckillItem(row.id)
  ElMessage.success('已删除')
  loadItems()
}

onMounted(async () => {
  await loadActivities()
  loadItems()
  const p = await getProductPage({ pageNum: 1, pageSize: 200 })
  products.value = p.records || []
})
</script>

<style scoped>
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.batch-items {
  width: 100%;
  max-height: 300px;
  overflow-y: auto;
}
.batch-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px dashed var(--el-border-color-light);
}
.bi-name {
  flex: 1;
  color: var(--el-text-color-primary);
  font-size: 13px;
}
.bi-original {
  margin-left: 8px;
  color: var(--el-text-color-regular);
  font-size: 12px;
}
.bi-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}
.bi-label {
  color: var(--el-text-color-regular);
  font-size: 12px;
}
</style>
