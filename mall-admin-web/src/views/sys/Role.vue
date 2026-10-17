<template>
  <div class="table-page">
    <div class="toolbar">
      <div class="spacer"></div>
      <el-button type="primary" @click="openAdd">新增角色</el-button>
    </div>

    <div class="panel-card">
      <el-table :data="list" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="角色名称" min-width="140" />
        <el-table-column prop="code" label="角色编码" min-width="140" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="visible" :title="editingId ? '编辑角色' : '新增角色'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" :disabled="!!editingId" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
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
import { getSysRoleList, addSysRole, updateSysRole, deleteSysRole } from '@/api'

const list = ref([])
const visible = ref(false)
const loading = ref(false)
const editingId = ref(null)
const formRef = ref()

const form = reactive({ name: '', code: '', description: '' })
const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const load = async () => {
  list.value = await getSysRoleList()
}

const openAdd = () => {
  editingId.value = null
  Object.assign(form, { name: '', code: '', description: '' })
  visible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  Object.assign(form, { name: row.name, code: row.code, description: row.description })
  visible.value = true
}

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      if (editingId.value) {
        await updateSysRole(editingId.value, { ...form })
      } else {
        await addSysRole({ ...form })
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
  await ElMessageBox.confirm(`确定删除角色「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteSysRole(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>
