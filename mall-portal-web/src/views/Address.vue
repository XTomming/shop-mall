<template>
  <div class="container address-page">
    <div class="head">
      <h2 class="page-title">收货地址</h2>
      <el-button type="primary" @click="openAdd">新增地址</el-button>
    </div>

    <div v-if="list.length" class="addr-list">
      <div v-for="a in list" :key="a.id" class="addr-card">
        <div class="addr-info">
          <div class="addr-top">
            <span class="receiver">{{ a.receiverName }}</span>
            <span class="phone">{{ a.phone }}</span>
            <el-tag v-if="a.isDefault === 1" size="small" type="warning">默认</el-tag>
          </div>
          <div class="addr-detail">{{ a.province }}{{ a.city }}{{ a.district }}{{ a.detailAddress }}</div>
        </div>
        <div class="addr-actions">
          <el-button v-if="a.isDefault !== 1" link type="primary" @click="setDefault(a)">设为默认</el-button>
          <el-button link type="primary" @click="openEdit(a)">编辑</el-button>
          <el-button link type="danger" @click="onDelete(a)">删除</el-button>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无收货地址" />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="visible" :title="editingId ? '编辑地址' : '新增地址'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="所在地区">
          <div class="region">
            <el-input v-model="form.province" placeholder="省" />
            <el-input v-model="form.city" placeholder="市" />
            <el-input v-model="form.district" placeholder="区" />
          </div>
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
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
import { getAddressList, addAddress, updateAddress, deleteAddress } from '@/api'

const list = ref([])
const visible = ref(false)
const loading = ref(false)
const editingId = ref(null)
const formRef = ref()

const emptyForm = () => ({
  receiverName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})
const form = reactive(emptyForm())

const rules = {
  receiverName: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const load = async () => {
  list.value = await getAddressList()
}

const openAdd = () => {
  editingId.value = null
  Object.assign(form, emptyForm())
  visible.value = true
}

const openEdit = (a) => {
  editingId.value = a.id
  Object.assign(form, a)
  visible.value = true
}

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const payload = { ...form }
      if (editingId.value) {
        await updateAddress(editingId.value, payload)
      } else {
        await addAddress(payload)
      }
      ElMessage.success('保存成功')
      visible.value = false
      load()
    } finally {
      loading.value = false
    }
  })
}

const setDefault = async (a) => {
  await updateAddress(a.id, { ...a, isDefault: 1 })
  load()
}

const onDelete = async (a) => {
  await ElMessageBox.confirm('确定删除该地址吗？', '提示', { type: 'warning' })
  await deleteAddress(a.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.address-page {
  margin-top: 20px;
  max-width: 900px;
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}
.addr-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.addr-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid var(--mall-border);
  display: flex;
  justify-content: space-between;
}
.addr-top {
  display: flex;
  align-items: center;
  gap: 8px;
}
.receiver {
  font-weight: 600;
  color: #333;
}
.phone {
  color: #666;
  font-size: 13px;
}
.addr-detail {
  margin-top: 10px;
  color: #555;
  font-size: 13px;
  line-height: 1.6;
}
.addr-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}
.region {
  display: flex;
  gap: 8px;
  width: 100%;
}
</style>
