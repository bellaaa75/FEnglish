<template>
  <div class="profile-info">
    <!-- 左侧：收藏列表（原内容） -->
    <div class="left-content">
      <!-- 加载 -->
      <div v-if="loading" class="loading">
        <el-icon size="26"><Loading /></el-icon>
        <p>加载中...</p>
      </div>

      <!-- 错误 -->
      <div v-else-if="error" class="error">
        <el-icon color="red"><Warning /></el-icon>
        <span>加载失败，请稍后再试</span>
      </div>

      <!-- 空状态 -->
      <div v-else-if="total === 0" class="empty">
        <el-icon size="48" color="#ccc"><Document /></el-icon>
        <p>暂无收藏的单词书</p>
      </div>

      <!-- 列表 -->
      <ul v-else class="book-list">
        <li v-for="item in books" :key="item.collectId" class="book-item">
          <div class="info">
            <div class="title">
              <el-icon><Notebook /></el-icon> <!-- 在这里插入图标 -->
              <router-link
                :to="{ name: 'BookDetailPlaza', params: { bookId: item.targetId } }"
                class="book-link"
              >
                {{ item.bookName }}
              </router-link>
            </div>
            <div class="meta">
              <span>收藏于：{{ fmt(item.collectTime) }}</span>
              <span>发布于：{{ fmt(item.publishTime) }}</span>
            </div>
          </div>
          <el-button type="danger" size="small" @click="handleUncollect(item.targetId)">
            <el-icon style="margin-right: 6px"><Delete /></el-icon>取消收藏
          </el-button>
        </li>
      </ul>

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        background
        layout="sizes,prev,pager,next,jumper,total"
        :total="total"
        :page-size="size"
        :current-page="page"
        :page-sizes="[5,10,15,20]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 右侧：操作栏（模仿 UserInfoForm） -->
    <div class="right-actions">
      <ActionSidebar />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Document, Loading, Warning, Notebook } from '@element-plus/icons-vue'
import ActionSidebar from '@/components/Sidebar/ActionSidebar.vue'   // 引入右侧栏
import request from '@/utils/request'

/* 分页 & 数据 */
const page = ref(1)
const size = ref(10)
const total = ref(0)
const books = ref([])
const loading = ref(false)
const error = ref(false)

/* 日期格式化 */
const fmt = (str) => (str ? new Date(str).toLocaleDateString('zh-CN') : '')

/* 加载数据 */
const loadBooks = async () => {
  loading.value = true
  error.value = false
  try {
    const res = await request.get('/api/collect/books', {
      params: { page: page.value - 1, size: size.value }
    })
    books.value = res.data.content || []
    total.value = res.data.totalElements || 0
  } catch (e) {
    error.value = true
    ElMessage.error(e?.message || '获取收藏失败')
  } finally {
    loading.value = false
  }
}

/* 取消收藏 */
const handleUncollect = async (bookId) => {
  try {
    await request.delete(`/api/collect/book/${bookId}`)
    ElMessage.success('已取消收藏')
    loadBooks()
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

/* 分页事件 */
const handleSizeChange = (val) => { size.value = val; page.value = 1; loadBooks() }
const handlePageChange = (val) => { page.value = val; loadBooks() }

onMounted(loadBooks)
</script>

<style scoped>
/* === 两栏布局：完全抄 UserInfoForm === */
.profile-info {
  display: flex;
  padding: 0;
  overflow-y: auto;
}
.left-content {
  flex: 1;
  padding: 30px;
}
.right-actions {
  width: 150px;
  flex-shrink: 0;
}

/* === 以下是你原来的列表/分页/加载样式，原封不动搬过来 === */
.loading,
.error,
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px 0;
}
.book-list {
  list-style: none;
  padding: 0;
  margin: 20px 0;
}
.book-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}
.info .title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 6px;
}
.meta {
  font-size: 14px;
  color: #666;
  display: flex;
  gap: 16px;
}

/* ====== 分页 ====== */
.el-pagination {
  margin-top: 20px;
  text-align: center;
  
}

.book-link {
  color: inherit;          /* 继承父级颜色 */
  text-decoration: none;   /* 去掉下划线 */
  transition: color 0.3s;
}
.book-link:hover {
  color: #409eff;          /* Element 主色 */
  text-decoration: underline;
}

.el-icon {
  margin-right: 8px; /* 图标和文字之间的间距 */
}
</style>