<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="title">Mall<span>后台管理</span></h1>
      <p class="subtitle">管理员登录</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password :prefix-icon="Lock" @keyup.enter="onSubmit" />
        </el-form-item>
        <el-button type="primary" class="submit" size="large" :loading="loading" @click="onSubmit">
          登 录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await userStore.login(form)
      ElMessage.success('登录成功')
      router.push(route.query.redirect || '/')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(ellipse at center, #1e293b 0%, #0f172a 100%);
}
.login-card {
  width: 400px;
  background: #1b2336;
  border: 1px solid #334155;
  border-radius: 14px;
  padding: 40px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.5);
}
.title {
  font-size: 26px;
  font-weight: 800;
  color: #ff5000;
  text-align: center;
}
.title span {
  color: #e2e8f0;
}
.subtitle {
  text-align: center;
  color: #64748b;
  margin: 8px 0 24px;
}
.submit {
  width: 100%;
  margin-top: 8px;
}
</style>
