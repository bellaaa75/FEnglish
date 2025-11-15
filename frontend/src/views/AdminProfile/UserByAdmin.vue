<template>
  <div class="profile-container">
    <el-container class="main-content">
      <!-- <el-header class="page-header">管理系统</el-header> -->
      <UserByAdmin />
    </el-container>
    <!-- 右侧操作栏（组件） -->
    <ActionSidebar />
  </div>
</template>

<script setup>
import ActionSidebar from '@/components/Sidebar/AdminActionSidebar.vue'
import UserByAdmin from '@/components/UserByAdminForm.vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { computed } from 'vue'

const store = useStore()
const router = useRouter()

// 路由守卫：未登录则跳转登录页
const isAuthenticated = computed(() => store.getters['user/isAuthenticated'])
if (!isAuthenticated.value) {
  router.push('/admin/login')
}
</script>

<style scoped>
.profile-container {
  display: flex;
  height: 100%;
}
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}
.page-header {
  height: 60px;
  line-height: 60px;
  padding: 0;
  background-color: #f5f7fa;
  border-bottom: 1px solid #eaecef;
  font-size: 18px;
  font-weight: 500;
  text-align: center;
}
</style>