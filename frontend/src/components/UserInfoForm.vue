<template>
  <div class="profile-info">
    <h3 class="section-title">个人信息</h3>
    <el-row :gutter="30" class="info-row">
      <!-- 用户ID -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">用户ID</label>
          <div class="info-value">{{ userInfo?.userId }}</div>
        </div>
      </el-col>
      <!-- 用户名 -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">用户名</label>
          <div class="info-value editable">{{ userInfo?.userName }}</div>
        </div>
      </el-col>
      <!-- 联系电话 -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">联系电话</label>
          <div class="info-value editable">{{ userInfo?.phoneNumber }}</div>
        </div>
      </el-col>
      <!-- 邮箱地址 -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">邮箱地址</label>
          <div class="info-value editable">{{ userInfo?.userMailbox }}</div>
        </div>
      </el-col>
      <!-- 性别 -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">性别</label>
          <div class="info-value editable">{{ userInfo?.gender }}</div>
        </div>
      </el-col>
      <!-- 注册时间 -->
      <el-col :span="12">
        <div class="info-item">
          <label class="info-label">注册时间</label>
          <div class="info-value">{{userInfo?.registerTime}}</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const userInfo = computed(() => store.getters['user/userInfo'])
</script>

<style scoped>
.profile-info {
  padding: 30px;
  overflow-y: auto;
}
.section-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 20px;
  color: #409eff;
}
.info-row {
  margin-bottom: 25px;
}
.info-item {
  margin-bottom: 15px;
}
.info-label {
  display: block;
  margin-bottom: 8px;
  color: #606266;
  font-size: 14px;
}
.info-value {
  padding: 8px 12px;
  border: 1px solid #eaecef;
  border-radius: 4px;
  font-size: 14px;
  background-color: #f5f7fa;
}
.editable {
  background-color: #fff;
  cursor: pointer;
  transition: border-color 0.3s;
}
.editable:hover {
  border-color: #409eff;
}
</style>