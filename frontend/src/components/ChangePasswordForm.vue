<template>
  <div class="profile-container">
    <el-container class="main-content">
      <el-row :gutter="30" class="info-row"></el-row>
        <el-header class="section-title">修改密码</el-header>
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
          <!-- 验证方式切换,这里先省略 -->
          <el-form-item>
            <div class="verify-switch">
              <span>请牢记新密码，谨慎修改</span>
              <el-button type="text" @click="toggleVerify"></el-button>
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
/* import { useRouter } from 'vue-router' */
import { ElMessage } from 'element-plus'


const store = useStore()
/* const router = useRouter() */
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
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请确认新密码'))
        } else if (value !== form.newPassword) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: ['blur', 'change'] // 添加change触发
    }
  ]
}

// 切换验证方式
const toggleVerify = () => {
  isPwdVerify.value = !isPwdVerify.value
  // 重置表单
  Object.assign(form, {
    oldPassword: '',
    newPassword: '',
    confirmNewPassword: ''
  })
  if (passwordForm.value) {
    passwordForm.value.clearValidate()
  }
}

// 处理修改密码逻辑
const handleChangePassword = async () => {
  try{
    await passwordForm.value.validate()
  }catch(validateError){
    const errorMsg = validateError.message || '填写有误'
    ElMessage.error(errorMsg)
    return
  }
  loading.value = true
  try {
    // 调用Vuex中修改密码的action
    await store.dispatch('user/changePassword', {
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    
    ElMessage.success('密码修改成功')
    
    // 重置表单
    Object.assign(form, {
      oldPassword: '',
      newPassword: '',
      confirmNewPassword: ''
    })
    passwordForm.value.clearValidate()
    
    /* // 如果需要登出并跳转登录页
    store.dispatch('user/logout')
    router.push('/user/login') */
    
  } catch (error) {
    console.log('密码修改错误：', error)
    
    // 更健壮的错误处理
    let errorMessage = '密码修改失败'
    
    if (error && error.response) {
      // HTTP 错误
      errorMessage = error.response.data?.message || errorMessage
    } else if (error && error.message) {
      // JavaScript 错误
      errorMessage = error.message
    } else if (typeof error === 'string') {
      // 字符串错误
      errorMessage = error
    }
    
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.info-row {
  margin-bottom: 25px;
}
.change-password {
  padding: 40px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}
.section-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 20px;
  color: #409eff;
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