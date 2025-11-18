<template>
  <div class="book-detail-container">
    <el-page-header @back="handleBack" />
    
    <el-card class="book-info-card" v-if="isBookLoaded">
      <div class="book-basic-info">
        <h2>{{ displayBookName }}</h2>
        <p>发布时间: {{ displayPublishTime }}</p>
        <p>单词总数: {{ displayTotalWords }} 个</p>
      </div>
    </el-card>

    <el-table :data="words" v-loading="loading" style="width:100%; margin-top:16px">
      <el-table-column prop="wordName" label="单词" width="180" />
      <el-table-column prop="partOfSpeech" label="词性" width="120" />
      <el-table-column prop="wordExplain" label="释义" />
      <!-- 操作列：学习和收藏按钮 -->
      <el-table-column label="操作" width="160">
        <template #default="scope">
          <div class="op-buttons">
            <el-button type="primary" size="small" style="margin-right: 8px;" @click="handleLearn(scope.row.wordId)">
              学习
            </el-button>

            <!-- 收藏/已收藏 -->
            <el-button
              :type="scope.row.collected ? 'success' : 'primary'"
              size="small"
              :disabled="scope.row.collected"
              @click="handleCollect(scope.row)">
              <el-icon><Star v-if="!scope.row.collected" /><StarFilled v-else /></el-icon>
              {{ scope.row.collected ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > pageInfo.pageSize"
      :current-page="pageInfo.currentPage"
      :page-size="pageInfo.pageSize"
      :total="total"
      @current-change="onPageChange"
      layout="prev, pager, next"
      style="margin-top:12px; text-align:right"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import vocabularyBookService from '@/services/vocabularyBookService';
import { Star, StarFilled } from '@element-plus/icons-vue';

import request from '@/utils/request' ;// 用于收藏接口


const route = useRoute();
const router = useRouter();
const bookId = route.params.bookId;
const bookInfo = ref({});
const words = ref([]);
const total = ref(0);
const loading = ref(false);
const pageInfo = ref({ currentPage: 1, pageSize: 20 });

/* ===== 新增：已收藏单词 id 集合 ===== */
const collectedWordIds = ref(new Set())

/* ===== 拉取用户收藏的单词 id（一次性） ===== */
const fetchCollectedWords = async () => {
  try {
    const res = await request.get('/api/collect/words', { params: { page: 0, size: 1000 } })
    const list = res.data?.content || []
    collectedWordIds.value = new Set(list.map(item => String(item.targetId)))
  } catch (e) {
    console.error('拉取单词收藏状态失败', e)
  }
}


const handleLearn = (wordId) => {
  if (!wordId) {
    ElMessage.warning('单词ID不存在');
    return;
  }
  // 跳转到LearnWord页面，传递单词ID参数
  router.push({
    name: 'LearnWord', // 确保路由名称与路由配置一致
    params: { wordId } // 传递当前单词的ID
  }).catch(err => {
    console.error('跳转学习页面失败:', err);
    ElMessage.error('无法跳转到学习页面');
  });
};

const displayBookName = computed(() => bookInfo.value?.bookName || '单词书详情');
const displayPublishTime = computed(() => {
  const t = bookInfo.value?.publishTime || '';
  return t ? new Date(t).toLocaleString() : '';
});
const displayTotalWords = computed(() => total.value || (Array.isArray(bookInfo.value?.wordList) ? bookInfo.value.wordList.length : 0));
const isBookLoaded = computed(() => Object.keys(bookInfo.value || {}).length > 0);

// onMounted(() => fetchBookDetail());
onMounted(async () => {
  await fetchBookDetail() // 原有逻辑
  await fetchCollectedWords() // 新增
  syncCollectedStatus() // 把已收藏回写到当前页
})

/* ===== 把后端返回的收藏状态回写到当前页 ===== */
const syncCollectedStatus = () => {
  words.value.forEach(w => {
    w.collected = collectedWordIds.value.has(String(w.wordId))
  })
}

/* ===== 收藏/取消收藏 ===== */
const handleCollect = async (row) => {
  if (row.collected) return
  try {
    await request.post(`/api/collect/word/${row.wordId}`)
    row.collected = true // 立即变已收藏
    collectedWordIds.value.add(String(row.wordId)) // 加入集合，翻页仍生效
    ElMessage.success('收藏成功')
  } catch (e) {
    ElMessage.error(e?.message || '收藏失败')
  }
}

const fetchBookDetail = async () => {
  if (!bookId) return;
  try {
    loading.value = true;
    const res = await vocabularyBookService.getBookDetail(bookId);
    bookInfo.value = res?.data || {};
    // 无论是否有wordList，都调用分页接口加载数据
    await fetchBookWords();
  } catch (e) {
    ElMessage.error('获取详情失败');
    router.back();
  } finally { loading.value = false; }
};

const fetchBookWords = async () => {
  try {
    loading.value = true;
    // 优先调用分页接口
    try {
      const res = await vocabularyBookService.getBookWords(bookId, pageInfo.value.currentPage, pageInfo.value.pageSize);
      const data = res?.data ?? res;
      if (data && Array.isArray(data.list)) {
        words.value = data.list;
        total.value = data.total ?? data.list.length;
        // 同步 bookInfo.wordList（若存在）
        if (Array.isArray(bookInfo.value?.wordList)) bookInfo.value.wordList = words.value;
        return { success: true, total: total.value };
      }
    } catch (err) {
      console.warn('getBookWords 调用失败，尝试使用 getBookDetail 回退', err);
    }

    // 回退：从详情接口读取 wordList 并进行前端分页
    try {
      const det = await vocabularyBookService.getBookDetail(bookId);
      const d = det?.data ?? det;
      if (d && Array.isArray(d.wordList)) {
        const all = d.wordList;
        total.value = all.length;
        // 前端分页切片
        const start = (pageInfo.value.currentPage - 1) * pageInfo.value.pageSize;
        const end = start + pageInfo.value.pageSize;
        words.value = all.slice(start, end);
        bookInfo.value = d;
        return { success: true, total: total.value };
      }
    } catch (err) {
      console.error('getBookDetail 回退也失败', err);
      throw err;
    }

    words.value = [];
    total.value = 0;
    return { success: true, total: 0 };
  } catch (err) {
    console.error('fetchBookWords 最终失败', err);
    ElMessage.error('加载单词失败');
    return { success: false, total: 0 };
  } finally {
    loading.value = false;
  }
  // ✅ 拿到最新 words 后再回写收藏状态
  syncCollectedStatus()
  return { success: true, total: total.value }
};

const ensureValidPageAndReload = async () => {
  const result = await fetchBookWords();
  const t = result.total || 0;
  const totalPages = Math.max(1, Math.ceil(t / pageInfo.value.pageSize));
  if (pageInfo.value.currentPage > totalPages) {
    pageInfo.value.currentPage = totalPages;
    await fetchBookWords();
  }
};

const onPageChange = (page) => { 
  pageInfo.value.currentPage = page; 
  ensureValidPageAndReload();
};

const handleBack = () => {
  const returnTo = route.query.returnTo;
  if (returnTo) router.push(String(returnTo)).catch(() => router.back()); else router.back();
};

</script>

<style scoped>
.book-detail-container { padding:16px; background:#fff; }
.book-info-card { margin-bottom:12px; }
/* 让操作列的两颗按钮始终并排不换行 */
.el-table__cell .cell {
  display: flex;
  gap: 8px;          /* 按钮间距 */
  white-space: nowrap;
}
.op-buttons {
  display: inline-flex;   /* 让容器本身不独占一行 */
  gap: 8px;               /* 按钮间距 */
  white-space: nowrap;    /* 强制不换行 */
  align-items: center;    /* 垂直居中 */
}
</style>