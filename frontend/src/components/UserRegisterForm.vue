<template>
  <el-form ref="registerForm" :model="form" :rules="rules" class="register-form">
    <el-form-item prop="userName">
      <el-input 
        v-model="form.userName" 
        placeholder="用户名" 
        prefix-icon="User"
      ></el-input>
    </el-form-item>
    <!-- 这里试图进行邮箱手机二选一,后面再加 -->
    <el-form-item prop="userMailbox">
      <el-input 
        v-model="form.userMailbox" 
        placeholder="电子邮箱地址" 
        prefix-icon="User"
      ></el-input>
    </el-form-item>
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
      >
        注册账号
      </el-button>
    </el-form-item>
    <div class="text-center mt-2">
      <el-button 
        type="default" 
        @click="$router.push('/user/login')"
        class="w-full"
      >
        返回登录
      </el-button>
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
const registerForm = ref(null)
const showPwd = ref(false)
const showConfirmPwd = ref(false)
const loading = ref(false)

const form = reactive({
  userName: '',
  userMailbox: '',
  userPassword: '',
  confirmPwd: ''
})

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  userMailbox: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { 
      validator: (rule, value) => {
        const reg = /^1[3-9]\d{9}$|^[\w.-]+@[\w.-]+\.[a-zA-Z]{2,}$/
        return reg.test(value) || '请输入有效的手机号或邮箱'
      },
      trigger: 'blur'
    }
  ],
  userPassword: [{ required: true, message: '请设置密码', trigger: 'blur' }],
  confirmPwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value) => value === form.userPassword || '两次密码不一致',
      trigger: 'blur'
    }
  ]
}

const handleRegister = async () => {
  await registerForm.value.validate()
  loading.value = true
  try {
    await store.dispatch('user/userRegister', form)
    ElMessage.success('注册成功')

    router.push('/user/login')
  } catch (error) {
    ElMessage.error(store.getters['user/authError'] || '注册失败')
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