<template>
  <div class="table-page">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="用户名" clearable style="width: 200px" @keyup.enter="search" />
      <el-button type="primary" @click="search">搜索</el-button>
      <div class="spacer"></div>
      <el-button type="primary" @click="openAdd">新增管理员</el-button>
    </div>

    <div class="panel-card">
      <el-table :data="list" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="nickname" label="昵称" min-width="140" />
        <el-table-column prop="role" label="角色" width="120" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch :model-value="row.status" :active-value="1" :inactive-value="0" @change="(v) => onStatus(row, v)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
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

    <el-dialog v-model="visible" :title="editingId ? '编辑管理员' : '新增管理员'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!editingId" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password :placeholder="editingId ? '留空则不修改' : '默认 123456'" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="form.role" placeholder="ADMIN" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSysUserPage, addSysUser, updateSysUser, deleteSysUser, updateSysUserStatus } from '@/api'

const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const visible = ref(false)
const loading = ref(false)
const editingId = ref(null)
const formRef = ref()

const form = reactive({ username: '', password: '', nickname: '', role: 'ADMIN', status: 1 })
const rules = { username: [{ required: true, message: '请输入用户名', trigger: 'blur' }] }

const load = async () => {
  const params = { pageNum: query.pageNum, pageSize: query.pageSize }
  if (query.keyword) params.keyword = query.keyword
  const page = await getSysUserPage(params)
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
  await updateSysUserStatus(row.id, status)
  ElMessage.success('操作成功')
}

const openAdd = () => {
  editingId.value = null
  Object.assign(form, { username: '', password: '', nickname: '', role: 'ADMIN', status: 1 })
  visible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  Object.assign(form, { username: row.username, password: '', nickname: row.nickname, role: row.role, status: row.status })
  visible.value = true
}

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      if (editingId.value) {
        await updateSysUser(editingId.value, { ...form })
      } else {
        await addSysUser({ ...form })
      }
      ElMessage.success('保存成功')
      visible.value = false
      load()
    } finally {
      loading.value = false
    }
  })
}

const onDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除管理员「${row.username}」吗？`, '提示', { type: 'warning' })
  await deleteSysUser(row.id)
  ElMessage.success('已删除')
  load()
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
