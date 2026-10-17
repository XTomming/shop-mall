<template>
  <div class="table-page">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="商品名称" clearable style="width: 220px" @keyup.enter="search" />
      <el-button type="primary" @click="search">搜索</el-button>
      <div class="spacer"></div>
      <el-button type="primary" @click="$router.push('/product/edit')">新增商品</el-button>
    </div>

    <div class="panel-card">
      <el-table :data="list" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="主图" width="80">
          <template #default="{ row }">
            <el-image :src="row.mainImage || PLACEHOLDER_IMG" fit="cover" style="width: 48px; height: 48px; border-radius: 6px" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="160" show-overflow-tooltip />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">{{ categoryMap[row.categoryId] || row.categoryId }}</template>
        </el-table-column>
        <el-table-column label="价格" width="100">
          <template #default="{ row }">¥{{ formatPrice(row.price) }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch :model-value="row.status" :active-value="1" :inactive-value="0" @change="(v) => onStatus(row, v)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/product/edit/${row.id}`)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductPage, deleteProduct, updateProductStatus, getCategoryList } from '@/api'
import { formatPrice, PLACEHOLDER_IMG } from '@/utils'

const list = ref([])
const total = ref(0)
const categoryMap = ref({})
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const flatten = (cats, map = {}) => {
  cats.forEach((c) => {
    map[c.id] = c.name
    if (c.children) flatten(c.children, map)
  })
  return map
}

const load = async () => {
  const params = { pageNum: query.pageNum, pageSize: query.pageSize }
  if (query.keyword) params.keyword = query.keyword
  const page = await getProductPage(params)
  list.value = page.records || []
  total.value = Number(page.total || 0)
}

const search = () => {
  query.pageNum = 1
  load()
}

const onPageChange = (p) => {
  query.pageNum = p
  load()
}

const onStatus = async (row, status) => {
  await updateProductStatus(row.id, status)
  ElMessage.success('操作成功')
}

const onDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除商品「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteProduct(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(async () => {
  load()
  categoryMap.value = flatten(await getCategoryList())
})
</script>

<style scoped>
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
