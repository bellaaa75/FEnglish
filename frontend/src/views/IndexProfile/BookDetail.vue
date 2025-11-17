<template>
  <div class="book-detail-container">
    <el-page-header 
      :content="['学习广场', bookInfo.bookName || '单词书详情']" 
      @back="handleBack" 
    />

    <el-card class="book-info-card" v-if="bookInfo">
      <div class="book-basic-info">
        <h2>{{ bookInfo.bookName }}</h2>
        <p>发布时间: {{ formatTime(bookInfo.publishTime) }}</p>
        <p>单词总数: {{ bookInfo.wordList.length }} 个</p>
      </div>
    </el-card>

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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import vocabularyBookService from '@/services/vocabularyBookService';

const route = useRoute();
const router = useRouter();
const bookInfo = ref(null);
const bookId = route.params.bookId;

onMounted(() => {
  fetchBookDetail();
});

// 获取单词书详情
const fetchBookDetail = async () => {
  try {
    const response = await vocabularyBookService.getBookDetail(bookId);
    bookInfo.value = response.data;
  } catch (error) {
    ElMessage.error('获取单词书详情失败: ' + error.message);
    router.back();
  }
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleString();
};

// 返回上一页
const handleBack = () => {
  router.back();
};

// 查看单词详情
const goToWordDetail = (wordId) => {
  router.push(`/words/detail/${wordId}`);
};
</script>

<style scoped>
.book-detail-container {
  padding: 20px;
}

.book-info-card {
  margin: 20px 0;
  padding: 20px;
}

.book-basic-info {
  line-height: 1.8;
}
</style>