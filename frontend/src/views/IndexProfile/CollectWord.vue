<template>
  <div class="profile-info">
    <!-- 左侧：按日期分组的单词收藏 -->
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
        <p>暂无收藏的单词</p>
      </div>

      <!-- 按日期分组展示 -->
      <div v-else class="group-wrap">
        <div
          v-for="[date, list] in groupedWords"
          :key="date"
          class="date-group"
        >
          <div class="date-title">{{ date }}</div>
          <ul class="word-list">
            <li v-for="w in list" :key="w.collectId" class="word-item">
              <router-link
                :to="{ name: 'WordDetail', params: { wordId: w.targetId } }"
                class="word-link"
              >
              <el-icon><CollectionTag  /></el-icon> <!-- 在这里插入图标 -->
                {{ w.wordName }}
              </router-link>
              <span class="first-explain">{{ firstExplain(w.wordExplain) }}</span>
              <el-button
                type="danger"
                size="small"
                @click="handleUncollect(w.targetId)"
              >
                <el-icon style="margin-right: 4px"><Delete /></el-icon>取消收藏
              </el-button>
            </li>
          </ul>
        </div>
      </div>

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

    <!-- 右侧：操作栏 -->
    <div class="right-actions">
      <ActionSidebar />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Document, Loading, Warning,CollectionTag } from '@element-plus/icons-vue'
import ActionSidebar from '@/components/Sidebar/ActionSidebar.vue'
import request from '@/utils/request'

/* 分页 & 原始数据 */
const page = ref(1)
const size = ref(10)
const total = ref(0)
const words = ref([])
const loading = ref(false)
const error = ref(false)
/* 只取第一个释义 */
const firstExplain = (exp) => (exp ? exp.split('；')[0] : '')

/* 日期格式化：yyyy-mm-dd */
const fmtDate = (str) => {
  if (!str) return ''
  const d = new Date(str)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(
    d.getDate()
  ).padStart(2, '0')}`
}

/* 分组：Map<yyyy-mm-dd, Array>  降序 */
const groupedWords = computed(() => {
  const map = new Map()
  words.value.forEach((item) => {
    const date = fmtDate(item.collectTime)
    if (!map.has(date)) map.set(date, [])
    map.get(date).push(item)
  })
  // 按日期降序
  return new Map([...map.entries()].sort((a, b) => b[0].localeCompare(a[0])))
})

/* 加载数据 */
const loadWords = async () => {
  loading.value = true
  error.value = false
  try {
    const res = await request.get('/api/collect/words', {
      params: { page: page.value - 1, size: size.value }
    })
    words.value = res.data.content || []
    total.value = res.data.totalElements || 0
  } catch (e) {
    error.value = true
    ElMessage.error(e?.message || '获取收藏失败')
  } finally {
    loading.value = false
  }
}

/* 取消收藏 */
const handleUncollect = async (wordId) => {
  try {
    await request.delete(`/api/collect/word/${wordId}`)
    ElMessage.success('已取消收藏')
    loadWords()
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

/* 分页事件 */
const handleSizeChange = (val) => { size.value = val; page.value = 1; loadWords() }
const handlePageChange = (val) => { page.value = val; loadWords() }

/* 初始化 */
loadWords()
</script>

<style scoped>
/* ====== 两栏布局 ====== */
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

/* ====== 分组样式 ====== */
.group-wrap {
  margin-bottom: 20px;
}
.date-group {
  margin-bottom: 24px;
}
.date-title {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 8px;
}
.word-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.word-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 6px;
  padding: 12px 16px;
  margin-bottom: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}
.word-link {
  font-size: 16px;
  color: #303133;
  text-decoration: none;
}
.word-link:hover {
  color: #409eff;
  text-decoration: underline;
}

/* ====== 分页 ====== */
.el-pagination {
  margin-top: 20px;
  text-align: center;
  
}

/* ====== 状态样式 ====== */
.loading,
.error,
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px 0;
}

.info-line {
  display: flex;
  align-items: center;
  gap: 2px;          /* 图标|单词|释义 之间间距 */
}
.first-explain {
  font-size: 14px;
  color: #909399;   /* Element 灰色 */
  margin-left: 4px; /* 再微调一点 */
}
</style>