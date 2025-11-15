<template>
    <el-container class="admin-content">
     <!--  <el-header class="page-title">管理系统功能</el-header> -->
      <el-main class="user-info">
        <h3>用户信息</h3>
        <!-- 搜索框 -->
        <el-input
          placeholder="请输入"
          prefix-icon="Search"
          style="width: 300px; margin-bottom: 20px;"
        />
        <!-- 用户列表 -->
        <el-card v-for="user in userList" :key="user.id" class="user-card">
          <el-avatar :size="40" icon="User" class="user-avatar" />
          <span class="user-name">{{ user.name }}</span>
          <span class="user-id">ID:{{ user.id }}</span>
          <span class="user-type">{{ user.type }}</span>
          <el-button type="text" class="btn-modify">修改信息</el-button>
          <el-button type="primary" class="btn-delete">删除用户</el-button>
        </el-card>
        <!-- 分页 -->
        <el-pagination
          background
          :current-page="2"
          :page-sizes="[5, 10, 15]"
          :page-size="5"
          layout="prev, pager, next"
          :total="20"
          style="margin-top: 20px;"
        />
      </el-main>
    </el-container>
</template>

<script setup>
/* import { User, Document, Collection, Search } from '@element-plus/icons-vue'; */
import { ref } from 'vue';
/* import { useRouter } from 'vue-router'; */
/* import { ElMessage } from 'element-plus'; */

// 模拟用户列表数据
const userList = ref([
  { name: 'AAA', id: '111111', type: '普通用户' },
  { name: 'BBB', id: '222222', type: '普通用户' },
  { name: 'CCC', id: '333333', type: '普通用户' },
  { name: 'DDD', id: '444444', type: '普通用户' }
]);

/* const router = useRouter(); */

</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
}

/* 左侧导航栏 */
.admin-sidebar {
  background-color: #f5f7fa;
  border-right: 1px solid #eaecef;
}

.admin-logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #000;
  border-bottom: 1px solid #eaecef;
}

.admin-menu {
  border-right: none;
  margin-top: 20px;
}

.admin-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

/* 中间内容区 */
.admin-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.page-title {
  height: 60px;
  line-height: 60px;
  padding: 0;
  background-color: #f5f7fa;
  border-bottom: 1px solid #eaecef;
  font-size: 18px;
  font-weight: 500;
  text-align: center;
}

.user-info {
  padding: 30px;
  overflow-y: auto;
}

.user-info h3 {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 20px;
  color: #000;
}

.user-card {
  display: flex;
  align-items: center;
  padding: 15px;
  margin-bottom: 10px;
  border: 1px solid #eaecef;
  border-radius: 4px;
}

.user-avatar {
  margin-right: 15px;
  background-color: #ddd;
}

.user-name {
  margin-right: 20px;
  font-weight: 500;
}

.user-id, .user-type {
  margin-right: 20px;
  color: #606266;
}

.btn-modify {
  margin-right: 10px;
  color: #409EFF;
}

.btn-delete {
  background-color: #F56C6C;
  border-color: #F56C6C;
  color: #fff;
}

/* 右侧操作栏（样式与之前一致） */
.action-sidebar {
  background-color: #f5f7fa;
  border-left: 1px solid #eaecef;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-sidebar .user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #409EFF;
  color: #fff;
  font-size: 24px;
  font-weight: bold;
  line-height: 60px;
  text-align: center;
  margin: 30px 0;
}

.action-sidebar .action-menu {
  border-right: none;
  width: 100%;
}

.action-sidebar .action-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  text-align: center;
  padding: 0;
}
</style>