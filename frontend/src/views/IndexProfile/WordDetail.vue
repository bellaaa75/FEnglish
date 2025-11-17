<template>
  <el-container class="word-detail-container">
    <el-main class="detail-content">
      <!-- 单词基本信息 -->
      <div class="word-basic">
        <h2 class="word-name">{{ wordDetail.wordName }} <span class="part-of-speech">{{ wordDetail.partOfSpeech || '' }}</span></h2>
        
        <!-- 单词形态分两行排列 -->
        <div class="word-forms-wrapper">
          <div class="form-row">
            <div class="form-item">
              <span class="form-label">第三人称现在式</span>
              <span class="form-value">{{ wordDetail.thirdPersonSingular || '无' }}</span>
            </div>
            <div class="form-item">
              <span class="form-label">现在分词</span>
              <span class="form-value">{{ wordDetail.presentParticiple || '无' }}</span>
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <span class="form-label">过去分词</span>
              <span class="form-value">{{ wordDetail.pastParticiple || '无' }}</span>
            </div>
            <div class="form-item">
              <span class="form-label">过去式</span>
              <span class="form-value">{{ wordDetail.pastTense || '无' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 单词释义 -->
      <div class="word-explain">
        <div class="section-title">单词释义</div>
        <p class="explain-content">{{ wordDetail.wordExplain || '无' }}</p>
      </div>

      <!-- 例句 -->
      <div class="word-example">
        <div class="section-title">例句</div>
        <div v-if="filteredExamples.length" class="example-container">
          <p v-for="(sentence, idx) in filteredExamples" :key="idx" class="example-text">
            {{ sentence }}
          </p>
        </div>
        <span v-else class="no-data">无</span>
      </div>

      <!-- 右下角返回按钮 -->
      <el-button type="primary" @click="goBack" class="fixed-back-btn">
        返回继续学习
      </el-button>
    </el-main>
  </el-container>
</template>

<script setup>
// 补充缺失的computed导入，并确保wordDetail已定义
import { ref, onMounted, computed } from 'vue';  // 关键：补充computed导入
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const BACKEND_BASE_URL = 'http://localhost:8080/api';

const route = useRoute();
const router = useRouter();
const wordDetail = ref({});  // 确保wordDetail在此处定义

// 修复例句处理逻辑，确保引用正确
const filteredExamples = computed(() => {
  if (!wordDetail.value.exampleSentence) return [];
  
  // 按数字序号拆分多个例句
  const rawExamples = wordDetail.value.exampleSentence.split(/(?=\d+\.)/);
  
  // 处理每个例句：清除换行、多余空格
  return rawExamples
    .map(example => {
      return example
        .replace(/[\n\r\t]/g, '')  // 清除换行、回车、制表符
        .replace(/\s+/g, ' ')      // 多个空格合并为一个
        .trim();                   // 清除首尾空格
    })
    .filter(example => example !== '');
});

const fetchWordDetail = async () => {
  const { wordId } = route.params;
  try {
    const response = await axios.get(`${BACKEND_BASE_URL}/words/${wordId}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      withCredentials: true
    });
    wordDetail.value = response.data || {};
  } catch (error) {
    console.error('获取单词详情失败:', error);
    if (error.response?.status === 404) {
      ElMessage.error(`单词ID ${wordId} 不存在`);
    } else if (error.response?.status === 401) {
      ElMessage.error('请先登录');
      router.push('/login');
    } else {
      ElMessage.error('加载失败');
    }
  }
};

const goBack = () => {
  router.back();
};

onMounted(fetchWordDetail);
</script>

<style scoped>
/* 样式部分保持不变 */
.word-detail-container {
  height: 100vh;
  background-color: #fff;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.detail-content {
  padding: 24px 20px;
  position: relative;
  max-width: 1000px;
  margin: 0 auto;
  width: 100%;
}

.word-basic {
  margin-bottom: 30px;
}
.word-name {
  font-size: 28px;
  font-weight: 700;
  color: #222;
  margin: 0 0 16px 0;
}
.part-of-speech {
  font-size: 18px;
  color: #666;
  margin-left: 10px;
  font-weight: normal;
}

.word-forms-wrapper {
  background-color: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 8px;
}
.form-row {
  display: flex;
  gap: 30px;
  margin-bottom: 12px;
}
.form-row:last-child {
  margin-bottom: 0;
}
.form-item {
  font-size: 16px;
}
.form-label {
  color: #888;
  margin-right: 6px;
}
.form-value {
  font-weight: 500;
  color: #333;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #222;
  margin-bottom: 12px;
  padding-bottom: 6px;
  border-bottom: 2px solid #f0f0f0;
}
.word-explain, .word-example {
  margin-bottom: 30px;
}
.explain-content {
  font-size: 16px;
  line-height: 1.8;
  color: #444;
  margin: 0;
  padding: 10px 0;
}

.example-container {
  margin: 0;
}
.example-text {
  font-size: 16px;
  line-height: 1.8;
  color: #444;
  margin: 0 0 12px 0;
  padding: 6px 0;
  white-space: normal;
  word-break: normal;
  word-wrap: break-word;
}
.no-data {
  font-size: 16px;
  color: #999;
  padding: 10px 0;
}

.fixed-back-btn {
  position: fixed;
  right: 20px;
  bottom: 28px;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 15px;
}
</style>