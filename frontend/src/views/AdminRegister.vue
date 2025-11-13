<template>
  <div class="auth-page">
    <div class="auth-container">
      <!-- 左侧图片区域 -->
      <div class="auth-image">
        <div class="brand-info">
          <h1>FEnglish Admin</h1>
          <p>管理员注册</p>
        </div>
      </div>
      <!-- 右侧表单区域 -->
      <div class="auth-form-wrapper">
        <div class="register-title">
          <h2>请设置管理员密码</h2>
        </div>
        <AdminRegisterForm />
      </div>
    </div>
  </div>
</template>

<script setup>
import AdminRegisterForm from '@/components/AdminRegisterForm.vue';
</script>

<style scoped>
/* 样式与 UserRegister.vue 基本相同，可复用 */
.auth-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.auth-container {
  display: flex;
  width: 100%;
  max-width: 900px;
  height: 550px; /* 高度适应表单 */
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

.auth-image {
  flex: 1;
  /* 可以使用不同的背景图来区分管理员页面 */
  background-image: url('@/assets/images/admin-bg.png');
  background-size: cover;
  background-position: center;
  position: relative;
}

.brand-info {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #fff;
  text-align: center;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.brand-info h1 {
  font-size: 36px;
  margin-bottom: 20px;
}

.brand-info p {
  font-size: 18px;
}

.auth-form-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px;
}

.register-title {
  margin-bottom: 30px;
  text-align: center;
}

.register-title h2 {
  font-size: 20px;
  color: #333;
  font-weight: 500;
}
</style>