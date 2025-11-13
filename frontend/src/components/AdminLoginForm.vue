<template>
  <el-form ref="loginForm" :model="form" :rules="rules" class="login-form">
    <el-form-item prop="identifier">
      <el-input 
        v-model="form.identifier" 
        placeholder="管理员ID" 
        prefix-icon="User"
      ></el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input 
        v-model="form.password" 
        type="password" 
        placeholder="密码" 
        prefix-icon="Lock"
        :show-password="showPwd"
        @click:suffix="showPwd = !showPwd"
      ></el-input>
    </el-form-item>
    <el-form-item>
      <el-button 
        type="primary" 
        class="w-full" 
        @click="handleLogin"
        :loading="loading"
      >
        管理员登录
      </el-button>
    </el-form-item>
    <div class="text-center mt-2">
      <el-link @click="$router.push('/admin/register')">管理员注册</el-link>
      <el-link @click="$router.push('/user/login')" type="info" class="ml-4">用户入口</el-link>
    </div>
  </el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const store = useStore()
const router = useRouter()
const loginForm = ref(null)
const showPwd = ref(false)
const loading = ref(false)

const form = reactive({
  identifier: '',
  password: ''
})

const rules = {
  identifier: [{ required: true, message: '请输入管理员ID', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await loginForm.value.validate()
  loading.value = true
  try {
    await store.dispatch('user/adminLogin', form)
    router.push('/admin/dashboard') // 替换为管理员首页路由
  } catch (error) {
    ElMessage.error(store.getters['user/authError'] || '管理员登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-form {
  max-width: 360px;
  margin: 0 auto;
}
.w-full {
  width: 100%;
}
</style>