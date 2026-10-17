<template>
  <div class="table-page">
    <div class="toolbar">
      <div class="spacer"></div>
      <el-button type="primary" @click="openAdd">新增公告</el-button>
    </div>

    <div class="panel-card">
      <el-table :data="list" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="240" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '发布' : '隐藏' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ (row.createTime || '').replace('T', ' ').slice(0, 16) }}</template>
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

    <el-dialog v-model="visible" :title="editingId ? '编辑公告' : '新增公告'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="60px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="5" />
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
import { getNoticePage, addNotice, updateNotice, deleteNotice } from '@/api'

const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10 })
const visible = ref(false)
const loading = ref(false)
const editingId = ref(null)
const formRef = ref()

const form = reactive({ title: '', content: '', status: 1 })
const rules = { title: [{ required: true, message: '请输入标题', trigger: 'blur' }] }

const load = async () => {
  const page = await getNoticePage({ pageNum: query.pageNum, pageSize: query.pageSize })
  list.value = page.records || []
  total.value = Number(page.total || 0)
}

const onPageChange = (p) => {
  query.pageNum = p
  load()
}

const openAdd = () => {
  editingId.value = null
  Object.assign(form, { title: '', content: '', status: 1 })
  visible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  Object.assign(form, { title: row.title, content: row.content, status: row.status })
  visible.value = true
}

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      if (editingId.value) {
        await updateNotice(editingId.value, { ...form })
      } else {
        await addNotice({ ...form })
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
  await ElMessageBox.confirm('确定删除该公告吗？', '提示', { type: 'warning' })
  await deleteNotice(row.id)
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
