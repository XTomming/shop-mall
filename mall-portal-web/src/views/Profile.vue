<template>
  <div class="container profile-page">
    <h2 class="page-title">个人中心</h2>

    <div class="card">
      <div class="card-title">基本资料</div>
      <el-form :model="form" label-width="80px" class="form">
        <el-form-item label="用户名">
          <el-input :model-value="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="onSave">保存资料</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="card">
      <div class="card-title">修改密码</div>
      <el-form :model="pwdForm" label-width="80px" class="form" ref="pwdRef" :rules="pwdRules">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="pwdLoading" @click="onChangePwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProfile, updateProfile, updatePassword } from '@/api'

const form = reactive({ username: '', nickname: '', phone: '', email: '', gender: 0 })
const saving = ref(false)

const pwdRef = ref()
const pwdForm = reactive({ oldPassword: '', newPassword: '' })
const pwdLoading = ref(false)
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

onMounted(async () => {
  const data = await getProfile()
  Object.assign(form, data)
})

const onSave = async () => {
  saving.value = true
  try {
    await updateProfile({ nickname: form.nickname, phone: form.phone, email: form.email, gender: form.gender })
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}

const onChangePwd = () => {
  pwdRef.value.validate(async (valid) => {
    if (!valid) return
    pwdLoading.value = true
    try {
      await updatePassword(pwdForm)
      ElMessage.success('密码修改成功')
      pwdForm.oldPassword = ''
      pwdForm.newPassword = ''
    } finally {
      pwdLoading.value = false
    }
  })
}
</script>

<style scoped>
.profile-page {
  margin-top: 20px;
  max-width: 720px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #333;
}
.card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
  border: 1px solid var(--mall-border);
}
.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--mall-border);
}
.form {
  max-width: 480px;
}
</style>
