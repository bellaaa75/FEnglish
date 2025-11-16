<template>
  <el-container class="word-edit-container">
    <el-main class="edit-content">
      <div class="edit-header">
        <el-button type="text" @click="goBack">
          <el-icon><arrow-left /></el-icon> 返回
        </el-button>
        <h3>编辑单词</h3>
      </div>

      <el-form
        ref="wordFormRef"
        :model="wordForm"
        label-width="120px"
        class="word-form"
        :rules="formRules"
      >
        <el-form-item label="单词ID" prop="wordId">
          <el-input v-model="wordForm.wordId" disabled />
        </el-form-item>
        <el-form-item label="单词名称" prop="wordName">
          <el-input v-model="wordForm.wordName" placeholder="请输入单词名称" />
        </el-form-item>
        <el-form-item label="词性" prop="partOfSpeech">
          <el-input v-model="wordForm.partOfSpeech" placeholder="如 noun/verb" />
        </el-form-item>
        <el-form-item label="第三人称现在式" prop="thirdPersonSingular">
          <el-input v-model="wordForm.thirdPersonSingular" placeholder="如 plays" />
        </el-form-item>
        <el-form-item label="现在分词" prop="presentParticiple">
          <el-input v-model="wordForm.presentParticiple" placeholder="如 playing" />
        </el-form-item>
        <el-form-item label="过去分词" prop="pastParticiple">
          <el-input v-model="wordForm.pastParticiple" placeholder="如 played" />
        </el-form-item>
        <el-form-item label="过去式" prop="pastTense">
          <el-input v-model="wordForm.pastTense" placeholder="如 played" />
        </el-form-item>
        <el-form-item label="单词释义" prop="wordExplain">
          <el-input
            type="textarea"
            v-model="wordForm.wordExplain"
            rows="3"
            placeholder="请输入单词释义"
          />
        </el-form-item>
        <el-form-item label="例句" prop="exampleSentence">
          <el-input
            type="textarea"
            v-model="wordForm.exampleSentence"
            rows="3"
            placeholder="请输入例句（可多条）"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">确认保存</el-button>
          <el-button type="text" @click="goBack" style="margin-left: 10px;">取消</el-button>
        </el-form-item>
      </el-form>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
// 1. 删除未使用的 ElMessageBox 导入
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton, ElIcon } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

// 路由相关
const route = useRoute();
const router = useRouter();

// 表单数据（与后端返回字段完全对应）
const wordForm = ref({
  wordId: '',
  wordName: '',
  partOfSpeech: '',
  thirdPersonSingular: '',
  presentParticiple: '',
  pastParticiple: '',
  pastTense: '',
  wordExplain: '',
  exampleSentence: '',
  wordID: '' // 后端返回的冗余字段，也绑定上避免遗漏
});

// 表单引用和校验规则
const wordFormRef = ref(null);
const formRules = ref({
  wordName: [{ required: true, message: '请输入单词名称', trigger: 'blur' }],
  wordExplain: [{ required: true, message: '请输入单词释义', trigger: 'blur' }]
});

// 页面加载时获取单词详情
onMounted(() => {
  const wordId = route.params.wordId;
  if (wordId) {
    fetchWordDetail(wordId);
  } else {
    ElMessage.error('未获取到单词ID');
    goBack();
  }
});

// 获取单词详情（核心修复：正确处理后端响应）
const fetchWordDetail = async (wordId) => {
  const token = localStorage.getItem('token');
  if (!token) {
    ElMessage.error('请先登录管理员账号');
    router.push('/admin/login');
    return;
  }

  try {
    const response = await axios.get(`/api/words/${wordId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    // 后端直接返回单词对象，无需解析嵌套，直接赋值
    const wordData = response.data;
    console.log('成功获取单词详情：', wordData);
    
    // 逐个字段赋值（确保表单双向绑定生效）
    wordForm.value.wordId = wordData.wordId || '';
    wordForm.value.wordName = wordData.wordName || '';
    wordForm.value.partOfSpeech = wordData.partOfSpeech || '';
    wordForm.value.thirdPersonSingular = wordData.thirdPersonSingular || '';
    wordForm.value.presentParticiple = wordData.presentParticiple || '';
    wordForm.value.pastParticiple = wordData.pastParticiple || '';
    wordForm.value.pastTense = wordData.pastTense || '';
    wordForm.value.wordExplain = wordData.wordExplain || '';
    wordForm.value.exampleSentence = wordData.exampleSentence || '';

    // 赋值后校验表单，确保无报错
    if (wordFormRef.value) {
      wordFormRef.value.clearValidate();
    }
  } catch (error) {
    console.error('获取单词详情失败：', error);
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录');
      localStorage.removeItem('token');
      router.push('/admin/login');
    } else if (error.response?.status === 404) {
      ElMessage.error('该单词不存在或已被删除');
    } else {
      ElMessage.error('获取单词详情失败：' + (error.message || '网络异常'));
    }
    goBack();
  }
};

// 提交表单（保存修改）
const submitForm = async () => {
  if (!wordFormRef.value) return;

  try {
    // 表单校验
    await wordFormRef.value.validate();

    const token = localStorage.getItem('token');
    if (!token) {
      ElMessage.error('请先登录');
      router.push('/admin/login');
      return;
    }

    // 2. 不定义 response 变量（因为没用到）
    await axios.put(`/api/words/${wordForm.value.wordId}`, wordForm.value, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    // 后端更新接口返回格式可能不同，按实际调整（成功即提示）
    ElMessage.success('保存成功！');
    // 延迟跳转，让用户看到提示
    setTimeout(() => {
      router.push('/profile/admin/wordlist');
    }, 1000);
  } catch (error) {
    console.error('保存失败：', error);
    if (error.name === 'Error') {
      // 表单校验失败，不额外提示
      return;
    }
    ElMessage.error('保存失败：' + (error.message || '网络异常'));
  }
};

// 返回单词列表页
const goBack = () => {
  router.push('/profile/admin/wordlist');
};
</script>

<style scoped>
.word-edit-container {
  height: 100%;
  width: 100%;
}

.edit-content {
  padding: 24px;
  background-color: #fff;
  min-height: calc(100vh - 64px);
}

.edit-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  border-bottom: 1px solid #eee;
  padding-bottom: 16px;
}

.edit-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.word-form {
  max-width: 800px;
  margin: 0 auto;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-textarea__inner {
  min-height: 100px !important;
  resize: vertical;
}

/* 适配响应式 */
@media (max-width: 768px) {
  .word-form {
    max-width: 100%;
  }
  .edit-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>