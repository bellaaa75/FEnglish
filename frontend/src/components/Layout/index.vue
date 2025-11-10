<template>
  <div class="layout">
    <!-- 顶部导航栏 -->
    <el-header style="background: #fff; border-bottom: 1px solid #eee;">
      <div style="float: right;">
        <el-button type="text" @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>
    <!-- 主体内容：侧边栏 + 页面内容 -->
    <div class="layout-body">
      <el-aside width="200px" style="background: #f5f7fa; height: calc(100vh - 60px);">
        <el-menu :default-active="$route.path" router>
          <el-menu-item index="/words">单词列表</el-menu-item>
          <el-menu-item index="/words/add">新增单词</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <slot /> <!-- 插槽：显示具体页面内容 -->
      </el-main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { ElMessage } from 'element-plus';

const router = useRouter();
const store = useStore();

// 退出登录
const handleLogout = () => {
  store.commit('setToken', '');
  store.commit('setUser', null);
  localStorage.removeItem('token');
  ElMessage.success('已退出登录');
  router.push('/login');
};
</script>

<style scoped>
.layout-body { display: flex; }
</style>