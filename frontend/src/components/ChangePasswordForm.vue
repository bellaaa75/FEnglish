<template>
  <div class="profile-container">
    <el-container class="main-content">
        <el-header class="page-header">修改密码</el-header>
        <el-main class="change-password">
        <el-form ref="passwordForm" :model="form" :rules="rules" class="password-form">
          <!-- 原密码 -->
          <el-form-item prop="oldPassword">
            <el-input 
              v-model="form.oldPassword" 
              type="password" 
              placeholder="请输入原密码" 
              prefix-icon="Lock"
              :show-password="showOldPwd"
              @click:suffix="showOldPwd = !showOldPwd"
            ></el-input>
          </el-form-item>
          <!-- 新密码 -->
          <el-form-item prop="newPassword">
            <el-input 
              v-model="form.newPassword" 
              type="password" 
              placeholder="请输入新密码" 
              prefix-icon="Lock"
              :show-password="showNewPwd"
              @click:suffix="showNewPwd = !showNewPwd"
            ></el-input>
          </el-form-item>
          <!-- 确认新密码 -->
          <el-form-item prop="confirmNewPassword">
            <el-input 
              v-model="form.confirmNewPassword" 
              type="password" 
              placeholder="确认密码" 
              prefix-icon="Lock"
              :show-password="showConfirmPwd"
              @click:suffix="showConfirmPwd = !showConfirmPwd"
            ></el-input>
          </el-form-item>
          <!-- 验证方式切换 -->
          <el-form-item>
            <div class="verify-switch">
              <span>请输入原密码</span>
              <el-button type="text" @click="toggleVerify">手机/邮箱验证</el-button>
            </div>
          </el-form-item>
          <!-- 确认按钮 -->
          <el-form-item>
            <el-button 
              type="primary" 
              class="w-full" 
              @click="handleChangePassword"
              :loading="loading"
            >
              确认
            </el-button>
          </el-form-item>
        </el-form>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'


const store = useStore()
const router = useRouter()
const passwordForm = ref(null)
const showOldPwd = ref(false)
const showNewPwd = ref(false)
const showConfirmPwd = ref(false)
const loading = ref(false)
const isPwdVerify = ref(true) // 标记当前是密码验证还是手机/邮箱验证

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})

const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmNewPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { 
      validator: (rule, value) => value === form.newPassword || '两次密码不一致',
      trigger: 'blur'
    }
  ]
}

// 切换验证方式（密码/手机邮箱）
const toggleVerify = () => {
  isPwdVerify.value = !isPwdVerify.value
  // 切换时重置表单（可选）
  passwordForm.value?.resetFields()
}

// 处理修改密码逻辑
const handleChangePassword = async () => {
  await passwordForm.value.validate()
  loading.value = true
  try {
    // 调用Vuex中修改密码的action（需在user.js中定义）
    await store.dispatch('user/changePassword', {
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    store.dispatch('user/logout') // 登出并跳转登录页
    router.push('/user/login')
  } catch (error) {
    ElMessage.error(store.getters['user/authError'] || '密码修改失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.change-password {
  padding: 40px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}
.password-form {
  max-width: 400px;
  width: 100%;
}
.verify-switch {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.verify-switch .el-button {
  padding: 0;
  color: #409EFF;
}
.w-full {
  width: 100%;
}
</style>