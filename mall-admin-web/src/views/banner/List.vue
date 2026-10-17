<template>
  <div class="table-page">
    <div class="toolbar">
      <div class="spacer"></div>
      <el-button type="primary" @click="openAdd">新增轮播图</el-button>
    </div>

    <div class="panel-card">
      <el-table :data="list" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="图片" width="160">
          <template #default="{ row }">
            <el-image :src="row.image || PLACEHOLDER_IMG" fit="cover" style="width: 120px; height: 48px; border-radius: 6px" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="140" />
        <el-table-column prop="link" label="跳转链接" min-width="140" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
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

    <el-dialog v-model="visible" :title="editingId ? '编辑轮播图' : '新增轮播图'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <el-upload :show-file-list="true" :limit="1" list-type="picture-card" :http-request="uploadBanner">
            <el-icon><Plus /></el-icon>
          </el-upload>
          <el-image v-if="form.image" :src="form.image" fit="cover" style="width: 120px; height: 48px; margin-left: 12px; border-radius: 6px" />
        </el-form-item>
        <el-form-item label="链接">
          <el-input v-model="form.link" placeholder="点击跳转链接（选填）" />
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
import { Plus } from '@element-plus/icons-vue'
import { getBannerPage, addBanner, updateBanner, deleteBanner, uploadFile } from '@/api'
import { PLACEHOLDER_IMG } from '@/utils'

const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10 })
const visible = ref(false)
const loading = ref(false)
const editingId = ref(null)
const formRef = ref()

const form = reactive({ title: '', image: '', link: '', sort: 0, status: 1 })
const rules = { image: [{ required: true, message: '请上传图片', trigger: 'change' }] }

const load = async () => {
  const page = await getBannerPage({ pageNum: query.pageNum, pageSize: query.pageSize })
  list.value = page.records || []
  total.value = Number(page.total || 0)
}

const onPageChange = (p) => {
  query.pageNum = p
  load()
}

const uploadBanner = async (options) => {
  try {
    const url = await uploadFile(options.file)
    form.image = url
    options.onSuccess(url)
  } catch (e) {
    options.onError(e)
  }
}

const openAdd = () => {
  editingId.value = null
  Object.assign(form, { title: '', image: '', link: '', sort: 0, status: 1 })
  visible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  Object.assign(form, { title: row.title, image: row.image, link: row.link, sort: row.sort, status: row.status })
  visible.value = true
}

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      if (editingId.value) {
        await updateBanner(editingId.value, { ...form })
      } else {
        await addBanner({ ...form })
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
  await ElMessageBox.confirm('确定删除该轮播图吗？', '提示', { type: 'warning' })
  await deleteBanner(row.id)
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
