<template>
  <el-form ref="registerForm" :model="form" :rules="rules" class="register-form">
    <el-form-item prop="userPassword">
      <el-input 
        v-model="form.userPassword" 
        type="password" 
        placeholder="设置密码" 
        prefix-icon="Lock"
        :show-password="showPwd"
        @click:suffix="showPwd = !showPwd"
      ></el-input>
    </el-form-item>
    <el-form-item prop="confirmPwd">
      <el-input 
        v-model="form.confirmPwd" 
        type="password" 
        placeholder="确认密码" 
        prefix-icon="Lock"
        :show-password="showConfirmPwd"
        @click:suffix="showConfirmPwd = !showConfirmPwd"
      ></el-input>
    </el-form-item>
    <el-form-item>
      <el-button 
        type="primary" 
        class="w-full" 
        @click="handleRegister"
        :loading="loading"
        style="
      background-color: #9332c7; 
      border-color: #9332c7;
    "
    :style="{
      '--el-button-hover-bg-color': '#7a28a8',
      '--el-button-active-bg-color': '#6b2296'
    }"
      >
        注册管理员账号
      </el-button>
    </el-form-item>
    <div class="text-center mt-2">
      <el-button 
        type="default" 
        @click="$router.push('/admin/login')"
        class="w-full"
      >
        返回管理员登录
      </el-button>
    </div>
  </el-form>
  <el-dialog v-model="dialogVisible" title="注册成功" width="500" center>
    <span>您的管理员用户ID是：{{ userId }}</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="dialogVisible = false; $router.push('/admin/login')">
          确认并登录
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
/* import { useRouter } from 'vue-router' */
import { ElMessage } from 'element-plus'

const store = useStore()
/* const router = useRouter() */
const registerForm = ref(null)
const showPwd = ref(false)
const showConfirmPwd = ref(false)
const loading = ref(false)
const dialogVisible = ref(false) 
const userId = ref('')

const form = reactive({
  userPassword: '',
  confirmPwd: ''
})

const rules = {
  userPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请确认新密码'))
        } else if (value !== form.userPassword) {
          callback(new Error('两次密码输入不一致，请重新输入'))
        } else {
          callback()
        }
      },
      trigger: ['blur', 'change'] // 添加change触发
    }
  ]
}


const handleRegister = async () => {
  try{
    await registerForm.value.validate()
  }catch(validateError){
    const errorMsg = validateError.message || '注册失败'
    ElMessage.error(errorMsg)
    return
  }
  loading.value = true
  try {
    const reUserId = await store.dispatch('user/adminRegister', form)
    userId.value = reUserId
    ElMessage.success('管理员注册成功')
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error(store.getters['user/authError'] || '管理员注册失败')
  } finally {
    loading.value = false
  }
}

</script>

<style scoped>
.register-form {
  max-width: 360px;
  margin: 0 auto;
}
.w-full {
  width: 100%;
}
</style>