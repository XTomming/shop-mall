<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="logo">Mall<span>客服工作台</span></div>
      <div class="right">
        <el-button size="small" @click="openRegister">注册客服</el-button>
        <div class="online-switch">
          <span class="online-label" :class="{ on: online }">{{ online ? '在线' : '离线' }}</span>
          <el-switch v-model="online" @change="onOnlineChange" />
        </div>
        <el-dropdown @command="onCommand">
          <span class="user">
            <el-icon><User /></el-icon>
            {{ userStore.nickname }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>

    <!-- 注册客服弹窗 -->
    <el-dialog v-model="registerVisible" title="注册客服账号" width="440px">
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入登录用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" show-password placeholder="默认 123456" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="registerForm.nickname" placeholder="请输入客服昵称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerVisible = false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="submitRegister">注册</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { setOnline, registerServiceUser } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const online = ref(userStore.userInfo?.online === 1)

const onOnlineChange = async (val) => {
  try {
    await setOnline(val)
    if (userStore.userInfo) {
      userStore.userInfo.online = val ? 1 : 0
      sessionStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    }
    ElMessage.success(val ? '已上线，可接收会话' : '已下线')
  } catch (e) {
    online.value = !val
  }
}

const onCommand = async (command) => {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    userStore.logout()
    router.push('/login')
  }
}

// ===== 注册客服 =====
const registerVisible = ref(false)
const registerLoading = ref(false)
const registerFormRef = ref()
const registerForm = reactive({ username: '', password: '', nickname: '' })
const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }]
}

const openRegister = () => {
  Object.assign(registerForm, { username: '', password: '', nickname: '' })
  registerVisible.value = true
}

const submitRegister = () => {
  registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    registerLoading.value = true
    try {
      await registerServiceUser({ ...registerForm })
      ElMessage.success('注册成功')
      registerVisible.value = false
    } finally {
      registerLoading.value = false
    }
  })
}
</script>

<style scoped>
.layout {
  height: 100%;
  flex-direction: column;
}
.header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #111827;
  border-bottom: 1px solid #1f2937;
  padding: 0 20px;
}
.logo {
  font-size: 18px;
  font-weight: 800;
  color: #ff5000;
}
.logo span {
  color: #e2e8f0;
  font-size: 14px;
  margin-left: 4px;
}
.right {
  display: flex;
  align-items: center;
  gap: 24px;
}
.online-switch {
  display: flex;
  align-items: center;
  gap: 8px;
}
.online-label {
  font-size: 13px;
  color: #94a3b8;
}
.online-label.on {
  color: #22c55e;
}
.user {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: #cbd5e1;
  font-size: 14px;
}
.main {
  padding: 0;
  overflow: hidden;
}
</style>
