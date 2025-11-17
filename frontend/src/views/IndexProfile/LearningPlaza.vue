<template>
  <div class="record-container">
    <h3>学习广场</h3>
    
    <!-- 搜索区域（对齐 VocabularyBookManage 的操作栏样式） -->
    <div class="operation-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索单词书名称"
        prefix-icon="Search"
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 单词书列表（添加 loading 状态，对齐数据渲染逻辑） -->
    <div class="book-list" v-loading="loading">
      <el-card 
        v-for="book in bookList" 
        :key="book.bookId" 
        class="book-card"
      >
        <template #header>
          <div class="book-header">
            <span 
              class="book-name" 
              @click="goToBookDetail(book.bookId)"
            >
              {{ book.bookName }}
            </span>
          </div>
        </template>
        <div class="book-info">
          <p>发布时间: {{ formatDate(book.publishTime) }}</p>
          <p>单词数量: {{ book.wordCount || 0 }} 个</p>
        </div>
      </el-card>

      <!-- 空状态提示 -->
      <div class="empty-tip" v-if="bookList.length === 0 && !loading">
        暂无匹配的单词书
      </div>
    </div>

    <!-- 分页控件（完全对齐 VocabularyBookManage 的分页配置） -->
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import vocabularyBookService from '@/services/vocabularyBookService';

const router = useRouter();

// ========== 完全对齐 VocabularyBookManage 的核心变量 ==========
// 搜索关键词（与前者一致）
const searchKeyword = ref('');
// 单词书列表数据（与前者一致）
const bookList = ref([]);
// 加载状态（新增，与前者一致）
const loading = ref(false);
// 分页信息（改为 ref 类型，与前者完全一致）
const pageInfo = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// ========== 复用 VocabularyBookManage 的日期格式化函数（兼容iOS解析bug） ==========
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) {
    dateString = dateString.replace(/-/g, '/');
    return new Date(dateString).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// ========== 对齐接口调用逻辑（关键修复：参数格式与前者一致） ==========
// 页面加载时获取单词书列表（与前者一致）
onMounted(() => {
  fetchBookList();
});

// 获取单词书列表（空关键词查询全部，参数格式：关键词、页码、每页条数）
const fetchBookList = async () => {
  try {
    loading.value = true;
    // 关键修复：按 VocabularyBookManage 的参数格式传递（关键词、页码、每页条数）
    const response = await vocabularyBookService.searchBooks(
      '', 
      pageInfo.value.currentPage, 
      pageInfo.value.pageSize
    );
    bookList.value = response.data.list || [];
    pageInfo.value.total = response.data.total || 0;
  } catch (error) {
    ElMessage.error('获取单词书列表失败：' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

// 搜索单词书（带关键词，参数格式与前者一致）
const searchBooks = async () => {
  try {
    loading.value = true;
    // 关键修复：按 VocabularyBookManage 的参数格式传递
    const response = await vocabularyBookService.searchBooks(
      searchKeyword.value.trim(),
      pageInfo.value.currentPage,
      pageInfo.value.pageSize
    );
    bookList.value = response.data.list || [];
    pageInfo.value.total = response.data.total || 0;
  } catch (error) {
    ElMessage.error('搜索失败：' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

// ========== 对齐分页、搜索操作逻辑 ==========
// 搜索触发（重置到第一页，与前者一致）
const handleSearch = () => {
  pageInfo.value.currentPage = 1;
  searchBooks();
};

// 重置搜索（恢复初始状态，与前者逻辑一致）
const resetSearch = () => {
  searchKeyword.value = '';
  pageInfo.value.currentPage = 1;
  fetchBookList();
};

// 分页大小改变（与前者一致）
const handleSizeChange = (size) => {
  pageInfo.value.pageSize = size;
  searchBooks();
};

// 当前页改变（与前者一致）
const handleCurrentChange = (page) => {
  pageInfo.value.currentPage = page;
  searchBooks();
};

// ========== 保留原有功能：详情页跳转 ==========
const goToBookDetail = (bookId) => {
  router.push(`/profile/plaza/book-detail/${bookId}`);
};
</script>

<style scoped>
.record-container {
  padding: 20px;
  background-color: #fff;
  min-height: calc(100vh - 40px); /* 对齐前者的页面高度 */
}

/* 对齐 VocabularyBookManage 的操作栏样式 */
.operation-bar {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

/* 单词书列表样式保留，添加加载状态 */
.book-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
  position: relative;
}

/* 加载状态覆盖层 */
.book-list[v-loading]::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.7);
  z-index: 10;
}

.book-card {
  height: 100%;
  transition: transform 0.2s;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.book-name {
  font-weight: bold;
  font-size: 16px;
  cursor: pointer;
  color: #409eff;
  text-decoration: underline;
}

.book-info {
  margin-top: 10px;
  color: #666;
  line-height: 1.8;
}

/* 空状态提示样式 */
.empty-tip {
  grid-column: 1 / -1;
  text-align: center;
  padding: 50px 0;
  color: #999;
  font-size: 14px;
}
</style>