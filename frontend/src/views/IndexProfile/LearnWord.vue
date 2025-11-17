<template>
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
        <!-- 新增：熟练掌握复选框 -->
        <div style="margin-top:12px;">
          <el-checkbox v-model="mastered" @change="onMasteredChange">
            熟练掌握
          </el-checkbox>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import learningStateService from '@/services/learningStateService'
import studyRecordService from '@/services/studyRecordService'

// 路由相关
const route = useRoute();
const router = useRouter();

// 获取并规范化 userId（处理可能误存为带引号的字符串）
const getUserId = () => {
  const raw = localStorage.getItem('userId');
  if (!raw) return null;
  try {
    // 如果是被 JSON.stringify 的字符串（如 "OU_xxx"），JSON.parse 会还原
    const parsed = JSON.parse(raw);
    if (typeof parsed === 'string') return parsed;
  } catch (e) {
    // ignore
  }
  // 兜底：去掉首尾双引号
  return raw.replace(/^"|"$/g, '');
}

// 数据状态
const currentWord = ref({});
const options = ref([]);
const selectedIndex = ref(null);
const mastered = ref(false);                // 复选框绑定
const learningStateExists = ref(false);     // 是否存在后端记录
let currentLearningState = null;            // 存放后端 LearningStateDTO

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
    // 加载当前用户该单词的学习状态（若用户已登录）
    await loadLearningState();

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

/**
 * 加载用户对当前单词的学习状态
 */
const loadLearningState = async () => {
  const userId = getUserId();
  const wordId = currentWord.value.wordId;
  console.log('[LearnWord] loadLearningState: userId =', userId, ', wordId =', wordId);
  if (!userId || !wordId) {
    console.warn('[LearnWord] userId or wordId missing, skipping loadLearningState');
    learningStateExists.value = false;
    mastered.value = false;
    return;
  }

  try {
    const res = await learningStateService.getLearningState(userId, wordId);
    console.log('[LearnWord] loadLearningState raw response:', res);
    // request 拦截器返回的是 { success: true, data: {...} }，需要访问 .data
    const dto = res?.data || null;
    if (dto) {
      learningStateExists.value = true;
      currentLearningState = dto;
      console.log('[LearnWord] loadLearningState: learnState =', dto.learnState, ', mastered =', dto.learnState === '熟练掌握');
      // 保持熟练掌握状态
      mastered.value = (dto.learnState === '熟练掌握');

      // 如果后端记录存在但为"未学"，则自动更新为"已学"
      if (dto.learnState === '未学') {
        console.log('[LearnWord] loadLearningState: found 未学, updating to 已学');
        const updateRes = await learningStateService.updateLearningState(userId, wordId, '已学');
        console.log('[LearnWord] updateLearningState(result):', updateRes);
        // 新增学习记录（与学习状态更新无关，前端单独调用 StudyRecord 接口）
        try {
          const srRes = await studyRecordService.addStudyRecord(userId, wordId, new Date());
          console.log('[LearnWord] studyRecord add result:', srRes);
        } catch (e) {
          console.error('[LearnWord] 创建 StudyRecord 失败:', e);
        }
        if (updateRes?.data === false) {
          // 如果更新失败，尝试新增后再更新
          console.log('[LearnWord] update failed, trying to add then update');
          const addRes = await learningStateService.addLearningState(userId, wordId);
          console.log('[LearnWord] addLearningState(result):', addRes);
          if (addRes?.data === false) {
            console.error('[LearnWord] 无法创建学习记录');
          } else {
            const updateRes2 = await learningStateService.updateLearningState(userId, wordId, '已学');
            console.log('[LearnWord] updateLearningState(after add) result:', updateRes2);
            if (updateRes2?.data === false) console.error('[LearnWord] 创建后仍无法更新为已学');
          }
        }
        // 将本地状态标记为已学（但不将 mastered 勾上）
        currentLearningState.learnState = '已学';
        mastered.value = false;
        ElMessage.info('已记录为已学');
      }
    } else {
      console.log('[LearnWord] loadLearningState: no learning state found, creating and marking 已学');
      learningStateExists.value = false;
      mastered.value = false;
      // 若无记录则新增并设置为已学
      const addRes = await learningStateService.addLearningState(userId, wordId);
      console.log('[LearnWord] addLearningState result:', addRes);
      if (addRes?.data === false) {
        console.error('[LearnWord] 后端新增学习记录失败');
      } else {
        const updateRes = await learningStateService.updateLearningState(userId, wordId, '已学');
        console.log('[LearnWord] updateLearningState after add result:', updateRes);
        if (updateRes?.data === false) {
          console.error('[LearnWord] 新增后更新为已学失败');
        } else {
          learningStateExists.value = true;
          currentLearningState = { userId, wordId, learnState: '已学' };
          // 新增对应的 StudyRecord（与 learningState 的新增/更新无强耦合）
          try {
            const srRes = await studyRecordService.addStudyRecord(userId, wordId, new Date());
            console.log('[LearnWord] studyRecord add result after addLearningState:', srRes);
          } catch (e) {
            console.error('[LearnWord] 创建 StudyRecord 失败(after add):', e);
          }
          ElMessage.info('已创建学习记录并标记为已学');
        }
      }
    }
  } catch (err) {
    console.error('[LearnWord] 加载学习状态失败:', err.message || err);
    learningStateExists.value = false;
    mastered.value = false;
  }
};

/**
 * 勾选/取消勾选回调
 */
const onMasteredChange = async (checked) => {
  const userId = getUserId();
  const wordId = currentWord.value.wordId;
  console.log('[LearnWord] onMasteredChange: checked =', checked, ', userId =', userId, ', wordId =', wordId);
  if (!userId || !wordId) {
    ElMessage.warning('请先登录后再修改学习状态');
    mastered.value = !!(currentLearningState && currentLearningState.learnState === '熟练掌握');
    return;
  }

  try {
    if (checked) {
      // 勾选"熟练掌握"
      console.log('[LearnWord] onMasteredChange: 勾选操作，learningStateExists =', learningStateExists.value);
      if (learningStateExists.value && currentLearningState) {
        // 情况1：记录存在 → 直接更新为"熟练掌握"
        console.log('[LearnWord] 记录存在，直接更新为熟练掌握');
        const updateRes = await learningStateService.updateLearningState(userId, wordId, '熟练掌握');
        console.log('[LearnWord] updateLearningState result:', updateRes);
        if (updateRes?.data === false) {
          // 如果更新失败，可能记录已被删除，尝试新增
          console.log('[LearnWord] 更新失败，尝试新增记录');
          const addRes = await learningStateService.addLearningState(userId, wordId);
          if (addRes?.data === false) {
            throw new Error('无法创建学习记录');
          }
          const updateRes2 = await learningStateService.updateLearningState(userId, wordId, '熟练掌握');
          if (updateRes2?.data === false) {
            throw new Error('无法更新学习状态为熟练掌握');
          }
        }
        currentLearningState.learnState = '熟练掌握';
        learningStateExists.value = true;
        ElMessage.success('已标记为熟练掌握');
      } else {
        // 情况2：记录不存在 → 先新增，再更新为"熟练掌握"
        console.log('[LearnWord] 记录不存在，先新增后更新为熟练掌握');
        const addRes = await learningStateService.addLearningState(userId, wordId);
        console.log('[LearnWord] addLearningState result:', addRes);
        if (addRes?.data === false) {
          throw new Error('后端新增失败，用户或单词可能不存在');
        }
        const updateRes = await learningStateService.updateLearningState(userId, wordId, '熟练掌握');
        console.log('[LearnWord] updateLearningState result:', updateRes);
        if (updateRes?.data === false) {
          throw new Error('后端更新为熟练掌握失败');
        }
        learningStateExists.value = true;
        currentLearningState = { userId, wordId, learnState: '熟练掌握' };
        ElMessage.success('已创建学习记录并标记为熟练掌握');
      }
    } else {
      // 取消勾选"熟练掌握" → 更新为"已学"
      console.log('[LearnWord] onMasteredChange: 取消勾选操作，learningStateExists =', learningStateExists.value);
      if (learningStateExists.value && currentLearningState) {
        // 情况1：记录存在 → 直接更新为"已学"
        console.log('[LearnWord] 记录存在，直接更新为已学');
        const updateRes = await learningStateService.updateLearningState(userId, wordId, '已学');
        console.log('[LearnWord] updateLearningState result:', updateRes);
        if (updateRes?.data === false) {
          // 如果更新失败，可能记录已被删除，尝试新增
          console.log('[LearnWord] 更新失败，尝试新增记录');
          const addRes = await learningStateService.addLearningState(userId, wordId);
          if (addRes?.data === false) {
            throw new Error('无法创建学习记录');
          }
          const updateRes2 = await learningStateService.updateLearningState(userId, wordId, '已学');
          if (updateRes2?.data === false) {
            throw new Error('无法更新学习状态为已学');
          }
        }
        currentLearningState.learnState = '已学';
        learningStateExists.value = true;
        ElMessage.success('已更新为已学');
      } else {
        // 情况2：记录不存在 → 先新增，再更新为"已学"
        console.log('[LearnWord] 记录不存在，先新增后更新为已学');
        const addRes = await learningStateService.addLearningState(userId, wordId);
        console.log('[LearnWord] addLearningState result:', addRes);
        if (addRes?.data === false) {
          throw new Error('后端新增失败，用户或单词可能不存在');
        }
        const updateRes = await learningStateService.updateLearningState(userId, wordId, '已学');
        console.log('[LearnWord] updateLearningState result:', updateRes);
        if (updateRes?.data === false) {
          throw new Error('后端更新为已学失败');
        }
        learningStateExists.value = true;
        currentLearningState = { userId, wordId, learnState: '已学' };
        ElMessage.success('已创建学习记录并标记为已学');
      }
    }
  } catch (err) {
    console.error('[LearnWord] 修改学习状态失败:', err.message || err);
    ElMessage.error('修改学习状态失败：' + (err.message || err));
    mastered.value = !!(currentLearningState && currentLearningState.learnState === '熟练掌握');
  }
};
</script>

<style scoped>
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
