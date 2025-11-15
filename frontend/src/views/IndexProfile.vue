<template>
  <div class="profile-container">
    <!-- 左侧导航栏（组件） -->
    <Sidebar />
    <!-- 中间内容区：用 <router-view> 渲染子页面（如UserInfo） -->
    <el-container class="main-content">
      <el-header class="page-header">
        <!-- 动态显示子页面标题（可通过路由meta配置） -->
        {{ $route.meta.title || '我的个人资料' }}
      </el-header>
      <!-- 子页面渲染出口：点击不同菜单，这里显示对应的页面 -->
      <router-view />
    </el-container>
  </div>
</template>

<script setup>
import Sidebar from '@/components/Sidebar/SearchIndex.vue'
import { useStore } from 'vuex'
import { computed } from 'vue'
import { useRouter } from 'vue-router'

// 初始化Vuex和路由实例
const store = useStore()
const router = useRouter()

// 路由守卫：未登录则跳转登录页
const isAuthenticated = computed(() => store.getters['user/isAuthenticated'])
if (!isAuthenticated.value) {
  router.push('/user/login')
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