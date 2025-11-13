<template>
  <div class="auth-page">
    <div class="auth-container">
      <!-- 左侧图片区域 -->
      <div class="auth-image">
        <div class="brand-info">
          <h1>FEnglish</h1>
          <p>开启英语词汇之旅，从这里出发</p>
        </div>
      </div>
      <!-- 右侧表单区域 -->
      <div class="auth-form-wrapper">
        <UserLoginForm />
      </div>
    </div>
  </div>
</template>

<script setup>
import UserLoginForm from '@/components/UserLoginForm.vue';
</script>

<style scoped>
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
  height: 500px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

.auth-image {
  flex: 1;
  background-image: url('@/assets/images/newspaper-bg.png');
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
  justify-content: center;
  align-items: center;
  padding: 40px;
}
</style>

