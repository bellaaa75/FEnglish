<template>
  <div>
    <!-- 卡片列表 -->
    <el-card v-for="(word, index) in words" :key="index" class="card">
      <template #header>
        <div class="card-header">
          <span>{{ word.collectTime }}</span> <!-- 显示收藏的日期 -->
        </div>
      </template>
      <div v-for="(item, idx) in word.items" :key="idx" class="text item">
        {{ item.wordName }} - {{ item.wordExplain }}
        <el-button size="mini" @click="unfavoriteWord(index, idx)">取消收藏</el-button>
      </div>
      <template #footer>
        <!-- Footer content为空，可以添加一些操作按钮或信息 -->
      </template>
    </el-card>

    <!-- 分页控件 -->
    <div class="pagination">
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
      />
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';

// 分页信息（与WordList保持一致的结构）
const pageInfo = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  totalPages: 0
});

const words = reactive([]);     // 单词数据

// 取消收藏单词的方法
const unfavoriteWord = async (dateIndex, wordIndex) => {
  const wordId = words[dateIndex].items[wordIndex].targetId;
  try {
    await request.delete(`/api/collect/word/${wordId}`);
    words[dateIndex].items.splice(wordIndex, 1); // 成功后移除单词
    // 如果当前日期分组为空，移除该分组
    if (words[dateIndex].items.length === 0) {
      words.splice(dateIndex, 1);
    }
    ElMessage.success('取消收藏成功');
    // 重新计算总页数
    pageInfo.total--;
    pageInfo.totalPages = Math.ceil(pageInfo.total / pageInfo.pageSize);
  } catch (error) {
    ElMessage.error(error.message || '取消收藏失败');
  }
};

// 从后端接口获取收藏的单词
const fetchCollectedWords = async () => {
  try {
    const response = await request.get('/api/collect/words', {
      params: {
        page: pageInfo.currentPage - 1, // 后端从0开始计数
        size: pageInfo.pageSize
      }
    });
    console.log(response.data); // 添加日志来查看响应内容
    const collectedWords = response.data.data.content;
    const groupedWords = collectedWords.reduce((acc, word) => {
      const date = word.collectTime.toISOString().split('T')[0]; // 提取日期部分
      if (!acc[date]) {
        acc[date] = { collectTime: date, items: [] };
      }
      acc[date].items.push(word);
      return acc;
    }, {});

    words.splice(0, words.length, ...Object.values(groupedWords));
    // 更新分页信息
    pageInfo.total = response.data.data.totalElements;
    pageInfo.totalPages = response.data.data.totalPages || Math.ceil(pageInfo.total / pageInfo.pageSize);
  } catch (error) {
    ElMessage.error(error.message || '获取收藏单词失败');
  }
};

// 每页条数改变
const handleSizeChange = (size) => {
  pageInfo.pageSize = size;
  pageInfo.currentPage = 1; // 重置到第一页
  fetchCollectedWords();
};

// 当前页改变
const handleCurrentChange = (page) => {
  pageInfo.currentPage = page;
  fetchCollectedWords();
};

// 在组件挂载时调用接口获取数据
onMounted(() => {
  fetchCollectedWords();
});
</script>