<template>
  <el-container class="word-list-container">
    <el-main class="word-content">
      <h3>单词管理</h3>
      
      <!-- 搜索区域 -->
      <div class="search-container">
        <el-input
          v-model="searchWord"
          placeholder="请输入单词搜索"
          prefix-icon="Search"
          style="width: 300px;"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="resetSearch">重置</el-button>
      </div>

      <!-- 单词列表 -->
      <el-table
        :data="wordList"
        border
        style="width: 100%; margin-top: 20px;"
        v-loading="loading"
        empty-text="暂无匹配单词"
      >
        <el-table-column
          prop="wordId"
          label="单词ID"
          width="180"
        />
        <el-table-column
          prop="wordName"
          label="单词"
          width="150"
        />
        <el-table-column
          prop="partOfSpeech"
          label="词性"
          width="100"
          :formatter="formatPartOfSpeech"
        />
        <el-table-column
          prop="wordExplain"
          label="释义"
          width="400"
        />
        <el-table-column
          label="操作"
          width="200"
        >
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.wordId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页控件 -->
      <el-pagination
        background
        :current-page="pageInfo.currentPage"
        :page-sizes="[5, 10, 15, 20]"
        :page-size="pageInfo.pageSize"
        :total="pageInfo.total"
        :total-pages="pageInfo.totalPages"
        layout="sizes, prev, pager, next, jumper, total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; text-align: right;"
      />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus'; // 删掉了 ElLoading 导入
import axios from 'axios';

// 搜索关键词
const searchWord = ref('');
// 加载状态
const loading = ref(false);

// 分页信息（与后端响应字段完全对应）
const pageInfo = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  totalPages: 0
});

// 单词列表数据
const wordList = ref([]);

// 初始化加载单词列表
onMounted(() => {
  fetchWordList();
});

// 格式化词性显示（处理null值）
const formatPartOfSpeech = (row) => {
  return row.partOfSpeech || '无';
};

// 获取Token（从localStorage读取，与登录逻辑保持一致）
const getToken = () => {
  return localStorage.getItem('token');
};

// 获取单词列表（单个请求添加Token，适配后端响应格式）
const fetchWordList = async () => {
  loading.value = true;
  const token = getToken();
  
  // 未登录则跳转登录页
  if (!token) {
    ElMessage.error('请先登录管理员账号');
    setTimeout(() => {
      window.location.href = '/admin/login';
    }, 1000);
    loading.value = false;
    return;
  }

  try {
    const params = {
      pageNum: pageInfo.value.currentPage,
      pageSize: pageInfo.value.pageSize
    };

    // 添加搜索参数（后端支持按wordName模糊搜索）
    if (searchWord.value.trim()) {
      params.wordName = searchWord.value.trim();
    }

    // 单个请求添加Token到Authorization头
    const response = await axios.get('/api/words', {
      params,
      headers: {
        Authorization: `Bearer ${token}` // 适配后端Token认证格式
      }
    });

    // 解析后端响应（关键：数据在response.data.data中）
    const resData = response.data;
    if (resData.success) {
      wordList.value = resData.data; // 单词列表数组
      pageInfo.value.total = resData.total; // 总条数（30）
      pageInfo.value.totalPages = resData.totalPages; // 总页数（3）
      pageInfo.value.currentPage = resData.currentPage; // 当前页
      pageInfo.value.pageSize = resData.pageSize; // 每页条数
    } else {
      ElMessage.error('获取单词列表失败：' + (resData.message || '接口返回失败'));
    }
  } catch (error) {
    // 错误处理（401代表Token过期/无效）
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录');
      localStorage.removeItem('token'); // 清除无效Token
      setTimeout(() => {
        window.location.href = '/admin/login';
      }, 1000);
    } else {
      ElMessage.error('获取单词列表失败：' + (error.message || '网络异常'));
    }
  } finally {
    loading.value = false;
  }
};

// 搜索处理
const handleSearch = () => {
  pageInfo.value.currentPage = 1; // 搜索后重置到第一页
  fetchWordList();
};

// 重置搜索
const resetSearch = () => {
  searchWord.value = '';
  pageInfo.value.currentPage = 1;
  fetchWordList();
};

// 每页条数改变
const handleSizeChange = (size) => {
  pageInfo.value.pageSize = size;
  pageInfo.value.currentPage = 1;
  fetchWordList();
};

// 当前页改变
const handleCurrentChange = (page) => {
  pageInfo.value.currentPage = page;
  fetchWordList();
};

// 编辑单词（跳转至编辑页面，需后续创建WordEdit.vue）
const handleEdit = (word) => {
  // 携带单词ID跳转，编辑页路径与路由配置一致
  window.location.href = `/profile/admin/wordedit/${word.wordId}`;
};

// 删除单词（单个请求添加Token）
const handleDelete = async (wordId) => {
  const token = getToken();
  if (!token) {
    ElMessage.error('请先登录');
    return;
  }

  try {
    await ElMessageBox.confirm(
      '确定要删除这个单词吗？删除后不可恢复！',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    // 删除请求添加Token
    const response = await axios.delete(`/api/words/${wordId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    if (response.data.success) {
      ElMessage.success('删除成功！');
      fetchWordList(); // 刷新列表
    } else {
      ElMessage.error('删除失败：' + (response.data.message || '删除接口返回失败'));
    }
  } catch (error) {
    // 忽略取消确认的错误
    if (error.name !== 'Error') {
      ElMessage.error('删除失败：' + (error.message || '网络异常'));
    }
  }
};
</script>

<style scoped>
.word-list-container {
  height: 100%;
  padding: 0 16px;
}

.word-content {
  padding: 20px 0;
}

.search-container {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
}

/* 优化表格单元格内边距和文字换行 */
::v-deep .el-table td {
  padding: 12px 8px;
}
::v-deep .el-table .cell {
  white-space: normal;
  word-wrap: break-word;
  word-break: break-all;
}

/* 加载状态样式 */
::v-deep .el-loading-mask {
  background-color: rgba(255, 255, 255, 0.7);
}
</style>