<template>
  <div class="table-page">
    <div class="toolbar">
      <div class="spacer"></div>
      <el-button type="primary" @click="openAdd(null)">新增一级分类</el-button>
    </div>

    <div class="panel-card">
      <el-table :data="list" row-key="id" default-expand-all :tree-props="{ children: 'children' }" style="width: 100%">
        <el-table-column prop="name" label="分类名称" min-width="180" />
        <el-table-column label="层级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.level === 1 ? 'primary' : 'info'" size="small">
              {{ row.level === 1 ? '一级' : '二级' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.level === 1" link type="primary" @click="openAdd(row)">新增子分类</el-button>
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="visible" :title="editingId ? '编辑分类' : '新增分类'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/api'

const list = ref([])
const visible = ref(false)
const loading = ref(false)
const editingId = ref(null)
const parentId = ref(0)
const formRef = ref()

const form = reactive({ name: '', parentId: 0, level: 1, sort: 0, status: 1 })
const rules = { name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }] }

const load = async () => {
  list.value = await getCategoryList()
}

const openAdd = (parent) => {
  editingId.value = null
  parentId.value = parent ? parent.id : 0
  form.parentId = parentId.value
  form.level = parent ? 2 : 1
  form.name = ''
  form.sort = 0
  form.status = 1
  visible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  form.name = row.name
  form.parentId = row.parentId
  form.level = row.level
  form.sort = row.sort
  form.status = row.status
  visible.value = true
}

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const payload = { name: form.name, parentId: form.parentId, level: form.level, sort: form.sort, status: form.status }
      if (editingId.value) {
        await updateCategory(editingId.value, payload)
      } else {
        await addCategory(payload)
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
  await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteCategory(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>
