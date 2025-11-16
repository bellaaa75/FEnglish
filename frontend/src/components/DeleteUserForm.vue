<template>
  <div class="profile-container">
    <!-- 中间内容区 -->
    <el-container class="main-content">
      <el-row :gutter="30" class="info-row"></el-row>
      <el-header class="section-title">注销账号</el-header>
      <el-main class="cancel-account">
        <div class="verify-form">
          <div class="verify-title">
            <span>请输入密码以验证身份</span>
            <el-button type="text" class="verify-switch"></el-button>
          </div>
          <el-form ref="passwordForm" :model="form" :rules="rules" class="password-form">
            <el-form-item prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码" 
                prefix-icon="Lock"
                :show-password="showPwd"
                @click:suffix="showPwd = !showPwd"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button 
                type="primary" 
                class="w-full" 
                @click="handleCancelAccount"
                :loading="loading"
              >
                确认注销
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useStore()
const router = useRouter()
const passwordForm = ref(null)
const showPwd = ref(false)
const loading = ref(false)

const form = reactive({
  password: ''
})

const rules = {
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleCancelAccount = async () => {
  await passwordForm.value.validate()
  loading.value = true
  try {
    // 弹框二次确认
    await ElMessageBox.confirm(
      '确认注销账号吗？注销后所有数据将不可恢复！',
      '警告',
      {
        confirmButtonText: '确认注销',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // 调用Vuex中注销账号的action（需在user.js中定义）
    await store.dispatch('user/cancelAccount', form.password)
    ElMessage.success('账号注销成功')

    // 判断是否为管理员并跳转到对应登录页,这里貌似不行
    const isAdmin = store.getters['user/isAdmin']
    if (isAdmin) {
      router.push('/admin/login') // 管理员跳转到管理员登录页
    } else {
      router.push('/user/login') // 普通用户跳转到普通登录页
    }
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消注销')
    } else {
      ElMessage.error(store.getters['user/authError'] || '账号注销失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.info-row {
  margin-bottom: 25px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 20px;
  color: #409eff;
}
.cancel-account {
  padding: 40px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}
.verify-form {
  width: 400px;
}
.verify-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.verify-switch {
  color: #409EFF;
  padding: 0;
}
.password-form {
  max-width: 300px;
  margin: 0 auto;
}
.w-full {
  width: 100%;
}
</style>