<template>
  <div class="table-page">
    <div class="panel-card form-card">
      <h3 class="form-title">{{ isEdit ? '编辑商品' : '新增商品' }}</h3>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subtitle" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-cascader
            v-model="categoryIds"
            :options="categories"
            :props="cascaderProps"
            style="width: 100%"
            @change="onCategoryChange"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" style="width: 200px" />
        </el-form-item>
        <el-form-item label="主图">
          <el-upload
            :show-file-list="true"
            :limit="1"
            list-type="picture-card"
            :http-request="uploadMain"
            :on-remove="() => (form.mainImage = '')"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="商品图集">
          <el-upload
            v-model:file-list="imageFileList"
            list-type="picture-card"
            multiple
            :http-request="uploadGallery"
            :on-remove="onImageRemove"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="详情">
          <el-input v-model="form.detail" type="textarea" :rows="5" placeholder="支持 HTML" />
        </el-form-item>
        <el-form-item label="上架">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">保存</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategoryList, getProductDetail, addProduct, updateProduct, uploadFile } from '@/api'

const route = useRoute()
const router = useRouter()

const formRef = ref()
const loading = ref(false)
const categories = ref([])
const categoryIds = ref([])
const imageFileList = ref([])
const cascaderProps = { value: 'id', label: 'name', children: 'children', checkStrictly: true }

const isEdit = computed(() => !!route.params.id)
const productId = computed(() => route.params.id)

const form = reactive({
  name: '',
  subtitle: '',
  categoryId: null,
  price: 0,
  originalPrice: null,
  stock: 0,
  mainImage: '',
  detail: '',
  status: 1,
  images: []
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

// 在分类树中查找某分类的完整路径
const findPath = (cats, targetId, path = []) => {
  for (const c of cats) {
    if (c.id === targetId) return [...path, c.id]
    if (c.children) {
      const r = findPath(c.children, targetId, [...path, c.id])
      if (r) return r
    }
  }
  return null
}

const onCategoryChange = (val) => {
  form.categoryId = val?.length ? val[val.length - 1] : null
}

const uploadMain = async (options) => {
  try {
    const url = await uploadFile(options.file)
    form.mainImage = url
    options.onSuccess(url)
  } catch (e) {
    options.onError(e)
  }
}

const uploadGallery = async (options) => {
  try {
    const url = await uploadFile(options.file)
    form.images.push(url)
    options.onSuccess(url)
  } catch (e) {
    options.onError(e)
  }
}

const onImageRemove = (file) => {
  const url = file.url || file.response
  form.images = form.images.filter((u) => u !== url)
}

const load = async () => {
  categories.value = await getCategoryList()
  if (isEdit.value) {
    const detail = await getProductDetail(productId.value)
    Object.assign(form, {
      name: detail.name,
      subtitle: detail.subtitle,
      categoryId: detail.categoryId,
      price: Number(detail.price),
      originalPrice: detail.originalPrice != null ? Number(detail.originalPrice) : null,
      stock: detail.stock,
      mainImage: detail.mainImage || '',
      detail: detail.detail || '',
      status: detail.status,
      images: detail.images || []
    })
    categoryIds.value = findPath(categories.value, detail.categoryId) || []
    imageFileList.value = (detail.images || []).map((url) => ({ name: url, url }))
  }
}

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const payload = { ...form, images: form.images }
      if (isEdit.value) {
        await updateProduct(productId.value, payload)
      } else {
        await addProduct(payload)
      }
      ElMessage.success('保存成功')
      router.push('/product/list')
    } finally {
      loading.value = false
    }
  })
}

onMounted(load)
</script>

<style scoped>
.form-card {
  max-width: 800px;
}
.form-title {
  font-size: 16px;
  font-weight: 700;
  color: #e2e8f0;
  margin-bottom: 24px;
}
</style>
