<template>
  <div class="table-page">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="用户名/昵称/手机号" clearable style="width: 240px" @keyup.enter="search" />
      <el-button type="primary" @click="search">搜索</el-button>
    </div>

    <div class="panel-card">
      <el-table :data="list" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="性别" width="70">
          <template #default="{ row }">{{ ['未知', '男', '女'][row.gender] || '未知' }}</template>
        </el-table-column>
        <el-table-column label="注册时间" width="170">
          <template #default="{ row }">{{ (row.createTime || '').replace('T', ' ').slice(0, 16) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch :model-value="row.status" :active-value="1" :inactive-value="0" @change="(v) => onStatus(row, v)" />
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
import { ElMessage } from 'element-plus'
import { getMemberPage, updateMemberStatus } from '@/api'

const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const load = async () => {
  const params = { pageNum: query.pageNum, pageSize: query.pageSize }
  if (query.keyword) params.keyword = query.keyword
  const page = await getMemberPage(params)
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
  await updateMemberStatus(row.id, status)
  ElMessage.success(status === 1 ? '已启用' : '已禁用')
}

onMounted(load)
</script>

<style scoped>
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
