<template>
  <el-container class="word-add-container">
    <el-main class="add-content">
      <div class="edit-header">
        <el-button type="text" @click="goBack">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <h3>添加单词</h3>
      </div>

      <el-form
        ref="wordFormRef"
        :model="wordForm"
        label-width="120px"
        class="word-form"
        :rules="formRules"
      >
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
          <el-button type="primary" @click="submitForm">确认添加</el-button>
          <el-button type="text" @click="goBack" style="margin-left: 10px;">取消</el-button>
        </el-form-item>
      </el-form>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import wordService from '@/services/wordService';

const router = useRouter();

// 表单数据
const wordForm = ref({
  wordName: '',
  partOfSpeech: '',
  thirdPersonSingular: '',
  presentParticiple: '',
  pastParticiple: '',
  pastTense: '',
  wordExplain: '',
  exampleSentence: ''
});

// 表单引用和校验规则
const wordFormRef = ref(null);
const formRules = ref({
  wordName: [{ required: true, message: '请输入单词名称', trigger: 'blur' }],
  wordExplain: [{ required: true, message: '请输入单词释义', trigger: 'blur' }]
});

// 提交表单（添加单词）
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

    // 调用添加单词接口
    const response = await wordService.addWord(wordForm.value);
    
    if (response.success) {
      ElMessage.success('单词添加成功！');
      // 延迟跳转，让用户看到提示
      setTimeout(() => {
        router.push('/profile/admin/wordlist');
      }, 1000);
    } else {
      ElMessage.error('添加失败：' + (response.message || '服务器异常'));
    }
  } catch (error) {
    console.error('添加单词失败：', error);
    if (error.name === 'Error') {
      // 表单校验失败，不额外提示
      return;
    }
    ElMessage.error('添加失败：' + (error.message || '网络异常'));
  }
};

// 返回单词列表页
const goBack = () => {
  router.push('/profile/admin/wordlist');
};
</script>

<style scoped>
.word-add-container {
  height: 100%;
  width: 100%;
}

.add-content {
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