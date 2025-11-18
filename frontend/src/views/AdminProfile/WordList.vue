<template>
  <el-container class="word-list-container">
    <el-main class="word-content">
      
      
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

        <!-- 添加单词按钮（固定到最右边） -->
        <el-button 
          type="primary" 
          @click="handleAddWord"
        >
          <el-icon><Plus /></el-icon> 添加单词
        </el-button>

      </div>

      <!-- 单词列表（优化列宽策略） -->
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
          width="120"
        />
        <el-table-column
          prop="partOfSpeech"
          label="词性"
          width="120"
          :formatter="formatPartOfSpeech"
        />
        <el-table-column
          prop="wordExplain"
          label="释义"
          width="600"
        />
        <el-table-column
          label="操作"
          align="left"
        >
          <template #default="scope">
            <el-button type="primary" size="small" style="margin-right: 8px;" @click="handleEdit(scope.row)">编辑</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { Plus } from '@element-plus/icons-vue';
const router = useRouter();

// 搜索关键词
const searchWord = ref('');
// 加载状态
const loading = ref(false);

// 分页信息
const pageInfo = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  totalPages: 0
});

// 单词列表数据（当前页展示数据）
const wordList = ref([]);
// 存储全部搜索结果（用于本地分页）
const allSearchResults = ref([]);

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
  
  if (!token) {
    ElMessage.error('请先登录');
    router.push('/admin/login');
    loading.value = false;
    return;
  }

  try {
    const params = {
      pageNum: pageInfo.value.currentPage,
      pageSize: pageInfo.value.pageSize
    };

    let requestUrl = '/api/words';
    const searchKey = searchWord.value.trim();
    let isSearchRequest = false; // 标记是否是搜索请求

    if (searchKey) {
      requestUrl = `/api/words/name/fuzzy/${encodeURIComponent(searchKey)}`;
      isSearchRequest = true; // 搜索请求：后端返回数组
    }

    const response = await axios.get(requestUrl, {
      params,
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    // 核心修复：根据请求类型解析响应 + 本地分页
    if (isSearchRequest) {
      // 搜索接口：后端返回全部结果，前端本地分页
      allSearchResults.value = response.data || []; // 存储全部搜索结果
      pageInfo.value.total = allSearchResults.value.length; // 总条数
      
      // 按当前页码和每页条数切割数据
      const startIndex = (pageInfo.value.currentPage - 1) * pageInfo.value.pageSize;
      const endIndex = startIndex + pageInfo.value.pageSize;
      wordList.value = allSearchResults.value.slice(startIndex, endIndex);
    } else {
      // 分页接口：后端返回分页数据，直接使用
      wordList.value = response.data.data || [];
      pageInfo.value.total = response.data.total || 0;
    }

    // 重新计算总页数
    pageInfo.value.totalPages = Math.ceil(pageInfo.value.total / pageInfo.value.pageSize);
  } catch (error) {
    console.error('请求失败：', error);
    if (error.response?.status === 404) {
      ElMessage.error(`接口不存在：${error.config.url}`);
    } else if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录');
      localStorage.removeItem('token');
      router.push('/admin/login');
    } else {
      ElMessage.error('获取单词失败：' + (error.message || '网络异常'));
    }
    wordList.value = [];
    allSearchResults.value = []; // 清空搜索结果
    pageInfo.value.total = 0;
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
  allSearchResults.value = []; // 清空搜索结果缓存
  pageInfo.value.currentPage = 1;
  fetchWordList();
};

// 每页条数改变（适配搜索结果本地分页）
const handleSizeChange = (size) => {
  pageInfo.value.pageSize = size;
  pageInfo.value.currentPage = 1; // 切换条数后回到第1页
  
  if (searchWord.value.trim()) {
    // 搜索状态：本地分页，无需请求后端
    const startIndex = 0;
    const endIndex = size;
    wordList.value = allSearchResults.value.slice(startIndex, endIndex);
    pageInfo.value.totalPages = Math.ceil(allSearchResults.value.length / size);
  } else {
    // 非搜索状态：请求后端分页
    fetchWordList();
  }
};

// 当前页改变（适配搜索结果本地分页）
const handleCurrentChange = (page) => {
  pageInfo.value.currentPage = page;
  
  if (searchWord.value.trim()) {
    // 搜索状态：本地分页，无需请求后端
    const startIndex = (page - 1) * pageInfo.value.pageSize;
    const endIndex = startIndex + pageInfo.value.pageSize;
    wordList.value = allSearchResults.value.slice(startIndex, endIndex);
  } else {
    // 非搜索状态：请求后端分页
    fetchWordList();
  }
};

// 添加单词跳转
const handleAddWord = () => {
  // 跳转到添加单词页面
  router.push('/profile/admin/wordadd');
};

// 编辑单词跳转
const handleEdit = (word) => {
  console.log('要编辑的单词ID：', word.wordId);
  if (!word.wordId) {
    ElMessage.error('单词ID不存在');
    return;
  }
  router.push({ name: 'AdminWordEdit', params: { wordId: word.wordId } });
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

/* 搜索区域：让添加按钮固定在最右边 */
.search-container {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
  justify-content: flex-start;
}
/* 关键样式：通过margin-left: auto将添加按钮推到最右侧 */
.search-container .el-button:last-child {
  margin-left: auto !important;
}

/* 优化表格单元格内边距和文字换行 */
::v-deep .el-table td {
  padding: 12px 8px;
}
/* 操作列按钮强制水平排列，禁止换行 */
::v-deep .el-table .cell {
  white-space: normal;
  word-wrap: break-word;
  word-break: break-all;
}
::v-deep .el-table .cell .el-button {
  display: inline-block !important;
  vertical-align: middle;
}

/* 加载状态样式 */
::v-deep .el-loading-mask {
  background-color: rgba(255, 255, 255, 0.7);
}
</style>