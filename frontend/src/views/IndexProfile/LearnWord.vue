<template>
  <!-- 模板部分不变 -->
  <el-container class="learn-word-container">
    <el-main class="learn-content">
      <div class="word-display">
        <h2>{{ currentWord.wordName }}</h2>
        <p class="part-of-speech">{{ currentWord.partOfSpeech || '无词性' }}</p>
      </div>

      <div class="options-container">
        <div 
          v-for="(option, index) in options" 
          :key="index"
          class="option-item"
          :class="{ 
            'correct': selectedIndex === index && option.isCorrect,
            'wrong': selectedIndex === index && !option.isCorrect,
            'selected': selectedIndex === index
          }"
          @click="handleOptionSelect(index)"
        >
          <span class="option-index">{{ String.fromCharCode(65 + index) }}.</span>
          <span class="option-text">{{ option.explain }}</span>
        </div>
      </div>

      <div class="action-buttons">
        <el-button 
          type="primary" 
          @click="goToDetail"
          :disabled="selectedIndex !== null"
        >
          直接查看释义
        </el-button>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';

// 路由相关
const route = useRoute();
const router = useRouter();

// 数据状态
const currentWord = ref({});
const options = ref([]);
const selectedIndex = ref(null);

// 修复：直接使用固定基础路径（去掉环境变量）
const BACKEND_BASE_URL = 'http://localhost:8080/api';

// 获取随机干扰单词
const getRandomWords = async (excludeWordId) => {
  try {
    const response = await axios.get(`${BACKEND_BASE_URL}/words`, {
      params: { pageNum: 1, pageSize: 10 },
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      withCredentials: true
    });

    const allWords = response.data?.data || [];
    return allWords
      .filter(word => 
        word.wordId !== excludeWordId && 
        word.wordExplain && 
        word.wordExplain.trim()
      )
      .sort(() => 0.5 - Math.random())
      .slice(0, 2);
  } catch (error) {
    console.error('获取干扰单词失败:', error);
    ElMessage.warning('干扰选项加载失败，使用默认选项');
    return [
      { wordExplain: 'to take or receive something offered' },
      { wordExplain: 'to give something in return for money' }
    ];
  }
};

// 初始化页面数据
const initData = async () => {
  const { wordId } = route.params;
  if (!wordId) {
    ElMessage.error('未指定单词ID');
    router.back();
    return;
  }

  try {
    const wordResponse = await axios.get(`${BACKEND_BASE_URL}/words/${wordId}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      withCredentials: true
    });

    currentWord.value = wordResponse.data || {};

    if (!currentWord.value.wordName || !currentWord.value.wordExplain) {
      ElMessage.error('单词数据不完整');
      router.back();
      return;
    }

    const randomWords = await getRandomWords(wordId);

    const optionList = [
      { explain: currentWord.value.wordExplain, isCorrect: true }
    ];
    randomWords.forEach(word => {
      optionList.push({ explain: word.wordExplain, isCorrect: false });
    });
    while (optionList.length < 3) {
      optionList.push({ explain: `选项 ${optionList.length + 1}（默认）`, isCorrect: false });
    }
    options.value = optionList.sort(() => 0.5 - Math.random());

  } catch (error) {
    console.error('初始化失败:', error);
    if (error.response?.status === 404) {
      ElMessage.error(`单词ID ${wordId} 不存在`);
    } else if (error.response?.status === 401) {
      ElMessage.error('请先登录');
      router.push('/login');
    } else {
      ElMessage.error('加载失败，请重试');
    }
  }
};

// 处理选项选择
const handleOptionSelect = (index) => {
  if (selectedIndex.value !== null) return;
  selectedIndex.value = index;
  setTimeout(goToDetail, 1000);
};

// 跳转详情页
const goToDetail = () => {
  router.push({
    path: `/profile/word-detail/${currentWord.value.wordId}`,
    query: { fromLearn: 'true' }
  });
};

onMounted(initData);
</script>

<style scoped>
/* 样式部分不变 */
.learn-word-container {
  height: 100vh;
  background-color: #f5f7fa;
}

.learn-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
}

.word-display {
  text-align: center;
  margin-bottom: 60px;
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.part-of-speech {
  color: #666;
  font-style: italic;
  margin-top: 10px;
}

.options-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 40px;
}

.option-item {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  border: 2px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
}

.option-item:hover {
  border-color: #c0c4cc;
}

.option-index {
  font-weight: bold;
  margin-right: 15px;
  color: #606266;
  min-width: 20px;
}

.option-text {
  flex: 1;
  font-size: 16px;
}

.option-item.selected {
  border-width: 3px;
}

.option-item.correct {
  border-color: #52c41a;
  background-color: rgba(82, 196, 26, 0.1);
}

.option-item.wrong {
  border-color: #f5222d;
  background-color: rgba(245, 34, 45, 0.1);
}

.action-buttons {
  text-align: center;
}
</style>