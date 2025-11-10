<template>
  <div class="login-container">
    <el-card title="英语单词学习系统登录">
      <el-form :model="loginForm" :rules="rules" ref="formRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import request from '../utils/request';
import { ElMessage } from 'element-plus';

const formRef = ref();
const loginForm = reactive({ username: '', password: '' });
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};
const router = useRouter();
const store = useStore();

const handleLogin = async () => {
  await formRef.value.validate();
  try {
    const res = await request.post('/user/login', loginForm);
    store.commit('setToken', res.token); // 保存token
    store.commit('setUser', res.user);   // 保存用户信息
    ElMessage.success('登录成功');
    router.push('/'); // 跳转到首页
  } catch (err) {
    ElMessage.error('用户名或密码错误');
  }
};
</script>

<style scoped>
.login-container { width: 400px; margin: 100px auto; }
</style>