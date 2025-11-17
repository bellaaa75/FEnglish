<template>
    <el-container class="admin-content">
     <!--  <el-header class="page-title">管理系统功能</el-header> -->
      <el-main class="user-info">
        <h3>用户信息</h3>
        <!-- 搜索框 -->
        <el-input
          v-model="keyword"
          placeholder="输入用户名/用户ID搜索"
          prefix-icon="Search"
          style="width: 300px; margin-bottom: 20px;"
          @keyup.enter="fetchUserList()"
        >
          <template #append>
            <el-button type="primary" @click="fetchUserList()">搜索</el-button>
          </template>
        </el-input>
        <!-- 用户列表 -->
        <el-card v-for="user in userList" :key="user.userId" class="user-card">
          <el-avatar :size="40" icon="User" class="user-avatar" />
          <span class="user-name">{{ user.userName }}</span>
          <span class="user-id">ID:{{ user.userId }}</span>
          <span class="user-type">{{ user.userType === 'ORDINARY' ? '普通用户' : '管理员' }}</span>
          <el-button type="text" class="btn-modify" @click="handleModify(user.userId)">详细信息</el-button>
          <el-button type="primary" class="btn-delete" @click="handleDelete(user.userId)">删除用户</el-button>
        </el-card>
        <!-- 分页 -->
         <!-- 前端1开始，后端0开始，+1转换 -->
          <!-- size-change 每页条数改变 -->
           <!-- current-change 页码改变 -->
        <el-pagination
          background
          :current-page="currentPage + 1" 
          :page-sizes="[4, 8, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalCount"
          @size-change="handleSizeChange" 
          @current-change="handleCurrentChange" 
          style="margin-top: 20px;"
        />
      </el-main>
    </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '@/utils/request' 
import { ElMessage, ElMessageBox } from 'element-plus';
// import { useRouter } from 'vue-router'; // 如需跳转可解开注释

// const router = useRouter();
// 1. 定义响应式变量（对接后端参数）
const keyword = ref(''); // 搜索关键词
const currentPage = ref(0); // 后端页码（从0开始）
const pageSize = ref(4); // 每页条数（默认4条，和后端一致）
const userList = ref([]); // 后端返回的用户列表
const totalCount = ref(0); // 总用户数
const loading = ref(false); // 加载状态（可选）

// 2. 核心：调用后端 GET 接口查询用户
const fetchUserList = async () => {
  loading.value = true;
  try {
    // 拼接 URL 参数（中文转码，避免乱码）
    const url = `/api/admin/list?keyword=${encodeURIComponent(keyword.value)}&page=${currentPage.value}&size=${pageSize.value}`;
    // 发起 GET 请求（如需权限，添加 Token 头）
    const res = await request.get(url);
    // 解析后端响应（和后端返回格式对应）
    userList.value = res.users; // 后端返回的 users 数组
    totalCount.value = res.totalCount; // 总用户数
  } catch (error) {
    console.error('查询用户失败：', error);
    ElMessage.error('查询失败，请稍后重试');
    userList.value = []; // 失败时清空列表
  } finally {
    loading.value = false;
  }
};

// 3. 分页事件：每页条数改变
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchUserList(); // 重新查询
};

// 4. 分页事件：页码改变
const handleCurrentChange = (val) => {
  currentPage.value = val - 1; // 前端页码1→后端0
  fetchUserList(); // 重新查询
};

// 5. 修改信息（跳转或弹窗，根据你的需求扩展）
const handleModify = (userId) => {
  console.log('详细信息：', userId);
  // 示例：跳转修改页面 → router.push(`/admin/user/modify?userId=${userId}`);
};

// 6. 删除用户（如需实现，对接后端删除接口）
const handleDelete = async (userId) => {
  try {
    await ElMessageBox.confirm('确定删除该用户？', '警告', {
      type: 'warning'
    });
    // 调用后端删除接口（示例）
    console.log('修改用户：', userId);
    // await axios.delete(`/api/user/admin/delete/${userId}`);
    ElMessage.success('删除成功');
    fetchUserList(); // 删除后刷新列表
  } catch (error) {
    ElMessage.info('已取消删除');
  }
};

// 7. 页面加载时默认查询第一页
onMounted(() => {
  fetchUserList();
});
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