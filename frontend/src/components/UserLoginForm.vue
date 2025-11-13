<template>
  <el-form ref="loginForm" :model="form" :rules="rules" class="login-form">
    <el-form-item prop="identifier">
      <el-input 
        v-model="form.identifier" 
        placeholder="用户名/手机号/邮箱" 
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
        登录
      </el-button>
    </el-form-item>
    <div class="text-center mt-2">
      <span>还没有账号? <el-link @click="$router.push('/user/register')">去注册</el-link></span>
      <el-link @click="$router.push('/admin/login')" type="info" class="ml-4">管理员入口</el-link>
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
  identifier: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginForm.value) {
    console.error('loginForm 引用为空')
    return
  }
  try {
    const valid = await loginForm.value.validate()
    if (!valid) return

    loading.value = true
    console.log('开始登录...')

    await store.dispatch('user/userLogin', form)
    console.log('登录成功:', store.getters['user/currentUser'])
    
    router.push('/home') // 替换为实际首页路由
  } catch (error) {
    console.log('错误对象:', error) // 打印完整错误对象
    console.log('Vuex 错误信息:', store.getters['user/authError']) 
    ElMessage.error(store.getters['user/authError'] || '登录失败')
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

<script>
export default {
  name: 'UserLoginForm'
}
</script>