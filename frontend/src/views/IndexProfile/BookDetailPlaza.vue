<template>
  <div class="book-detail-container">
    <el-page-header :content="['学习广场', displayBookName]" @back="handleBack" />
    <el-card class="book-info-card" v-if="isBookLoaded">
      <div class="book-basic-info">
        <h2>{{ displayBookName }}</h2>
        <p>发布时间: {{ displayPublishTime }}</p>
        <p>单词总数: {{ displayTotalWords }} 个</p>
        <el-button type="primary" @click="startLearning">开始学习</el-button>
      </div>
    </el-card>

    <el-table :data="words" v-loading="loading" style="width:100%; margin-top:16px">
      <el-table-column prop="wordName" label="单词" />
      <el-table-column prop="partOfSpeech" label="词性" />
      <el-table-column prop="wordExplain" label="释义" />
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

const route = useRoute();
const router = useRouter();
const bookId = route.params.bookId;
const bookInfo = ref({});
const words = ref([]);
const total = ref(0);
const loading = ref(false);
const pageInfo = ref({ currentPage: 1, pageSize: 20 });

const displayBookName = computed(() => bookInfo.value?.bookName || '单词书详情');
const displayPublishTime = computed(() => {
  const t = bookInfo.value?.publishTime || '';
  return t ? new Date(t).toLocaleString() : '';
});
const displayTotalWords = computed(() => total.value || (Array.isArray(bookInfo.value?.wordList) ? bookInfo.value.wordList.length : 0));
const isBookLoaded = computed(() => Object.keys(bookInfo.value || {}).length > 0);

onMounted(() => fetchBookDetail());

const fetchBookDetail = async () => {
  if (!bookId) return;
  try {
    loading.value = true;
    const res = await vocabularyBookService.getBookDetail(bookId);
    bookInfo.value = res?.data || {};
    if (Array.isArray(bookInfo.value.wordList)) {
      words.value = bookInfo.value.wordList;
      total.value = words.value.length;
    } else {
      await fetchBookWords();
    }
  } catch (e) {
    ElMessage.error('获取详情失败');
    router.back();
  } finally { loading.value = false; }
};

const fetchBookWords = async () => {
  try {
    loading.value = true;
    const res = await vocabularyBookService.getBookWords(bookId, pageInfo.value.currentPage, pageInfo.value.pageSize);
    words.value = res?.data?.list || [];
    total.value = res?.data?.total || words.value.length;
  } catch {
    ElMessage.error('加载单词失败');
  } finally { loading.value = false; }
};

const onPageChange = (page) => { pageInfo.value.currentPage = page; fetchBookWords(); };

const handleBack = () => {
  const returnTo = route.query.returnTo;
  if (returnTo) router.push(String(returnTo)).catch(() => router.back()); else router.back();
};

// 跳转到 LearnWord 页面（假设路由名为 LearnWord，传 bookId）
const startLearning = () => {
  router.push({ name: 'LearnWord', params: { bookId } });
};
</script>

<style scoped>
.book-detail-container { padding:16px; background:#fff; }
.book-info-card { margin-bottom:12px; }
</style>