<template>
  <div class="book-detail-container">
    <el-page-header 
      :content="['学习广场', displayBookName || '单词书详情']" 
      @back="handleBack" 
    />

    <el-card class="book-info-card" v-if="isBookLoaded">
      <div class="book-basic-info">
        <h2>{{ displayBookName }}</h2>
        <p>发布时间: {{ displayPublishTime }}</p>
        <p>单词总数: {{ displayTotalWords }} 个</p>
      </div>
    </el-card>

    <!-- 加载占位或空状态 -->
    <div v-else-if="loading" class="loading">加载中...</div>
    <div v-else class="empty-tip">未能获取单词书信息</div>

    <!-- 单词列表 -->
    <h3 style="margin: 20px 0;">单词列表</h3>
    <el-table
      :data="bookInfo.wordList"
      border
      style="width: 100%;"
      empty-text="该单词书暂无单词"
    >
      <el-table-column prop="wordId" label="ID" width="80" />
      <el-table-column prop="wordName" label="单词" width="120" />
      <el-table-column prop="partOfSpeech" label="词性" width="100" />
      <el-table-column prop="wordExplain" label="释义" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button size="small" @click="goToWordDetail(scope.row.wordId)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="total > pageInfo.pageSize"
      background
      :current-page="pageInfo.currentPage"
      :page-size="pageInfo.pageSize"
      :total="total"
      layout="prev, pager, next, jumper"
      @current-change="onPageChange"
      style="margin-top: 16px; text-align: right;"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import vocabularyBookService from '@/services/vocabularyBookService';

const route = useRoute();
const router = useRouter();
const bookInfo = ref({});
const words = ref([]);
const total = ref(0);
const loading = ref(false);
const bookId = route.params.bookId || route.params.id;

// 支持分页请求（若后端只返回基本信息）
const pageInfo = ref({ currentPage: 1, pageSize: 20 });

const displayBookName = computed(() => bookInfo.value?.bookName || '单词书详情');
const displayPublishTime = computed(() => {
  const t = bookInfo.value?.publishTime || bookInfo.value?.createdAt || '';
  if (!t) return '';
  try { return new Date(t).toLocaleString(); } catch { return String(t); }
});
const displayTotalWords = computed(() => {
  // 优先使用后端单独返回的 total，再 fallback 到 bookInfo.wordList 长度
  return total.value || (Array.isArray(bookInfo.value?.wordList) ? bookInfo.value.wordList.length : 0);
});
const isBookLoaded = computed(() => Object.keys(bookInfo.value || {}).length > 0);

onMounted(() => {
  fetchBookDetail();
});

// 获取单词书详情
const fetchBookDetail = async () => {
  if (!bookId) {
    ElMessage.error('缺少单词书 ID');
    handleBack();
    return;
  }
  try {
    loading.value = true;
    const response = await vocabularyBookService.getBookDetail(bookId);
    // 情况 A：后端直接返回包含 wordList
    if (response?.data && Array.isArray(response.data.wordList)) {
      bookInfo.value = response.data;
      words.value = response.data.wordList || [];
      total.value = response.data.wordList.length || 0;
    } else {
      // 情况 B：仅返回基本信息，单独分页请求单词列表
      bookInfo.value = response?.data || {};
      await fetchBookWords();
    }
  } catch (error) {
    ElMessage.error('获取单词书详情失败: ' + (error?.message || '未知错误'));
    handleBack();
  } finally {
    loading.value = false;
  }
};

// ========== 新增：获取单词书中的单词列表（支持分页）==========
const fetchBookWords = async () => {
  try {
    loading.value = true;
    const res = await vocabularyBookService.getBookWords(
      bookId,
      pageInfo.value.currentPage,
      pageInfo.value.pageSize
    );
    // 根据实际后端返回字段名调整（这里假设为 list 和 total）
    words.value = res?.data?.list || res?.data?.words || [];
    total.value = res?.data?.total ?? (Array.isArray(words.value) ? words.value.length : 0);
  } catch (err) {
    ElMessage.error('加载单词失败：' + (err?.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

const onPageChange = (page) => {
  pageInfo.value.currentPage = page;
  fetchBookWords();
};

// 返回：优先使用 query.returnTo，否则 router.back()
const handleBack = () => {
  const returnTo = route.query.returnTo;
  if (returnTo) {
    router.push(String(returnTo)).catch(() => router.back());
  } else {
    router.back();
  }
};

// 查看单词详情
const goToWordDetail = (wordId) => {
  if (!wordId) return;
  router.push({ name: 'WordDetail', params: { wordId } });
};
</script>

<style scoped>
.book-detail-container { padding: 16px; background: #fff; }
.book-info-card { margin-bottom: 12px; }
.book-basic-info h2 { margin: 0 0 8px; }
.loading { padding: 20px; text-align: center; }
.empty-tip { color: #999; padding: 20px; text-align: center; }
</style>