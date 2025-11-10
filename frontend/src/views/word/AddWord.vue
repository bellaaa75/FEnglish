<template>
  <div class="add-word-container">
    <el-card title="增加单词">
      <el-form :model="wordForm" :rules="rules" ref="wordFormRef" label-width="100px">
        <!-- 单词输入框 -->
        <el-form-item label="单词" prop="wordName">
          <el-input v-model="wordForm.wordName" placeholder="请输入单词（如 apple）" />
        </el-form-item>

        <!-- 词性选择 -->
        <el-form-item label="词性" prop="partOfSpeech">
          <el-select v-model="wordForm.partOfSpeech" placeholder="请选择词性">
            <el-option label="名词" value="n." />
            <el-option label="动词" value="v." />
            <el-option label="形容词" value="adj." />
            <el-option label="副词" value="adv." />
          </el-select>
        </el-form-item>

        <!-- 释义输入 -->
        <el-form-item label="释义" prop="wordExplain">
          <el-input
            v-model="wordForm.wordExplain"
            type="textarea"
            rows="3"
            placeholder="请输入单词释义（如 苹果）"
          />
        </el-form-item>

        <!-- 示例句子 -->
        <el-form-item label="示例" prop="exampleSentence">
          <el-input
            v-model="wordForm.exampleSentence"
            type="textarea"
            rows="2"
            placeholder="请输入示例句子（如 I like apples.）"
          />
        </el-form-item>

        <!-- 提交/重置按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElForm, ElFormItem } from 'element-plus';
import { ElMessage } from 'element-plus';
import request from '@/utils/request'; // 导入封装的Axios

// 表单数据
const wordForm = reactive({
  wordName: '', // 单词名称
  partOfSpeech: '', // 词性
  wordExplain: '', // 释义
  exampleSentence: '' // 示例句子
});

// 表单验证规则
const rules = {
  wordName: [{ required: true, message: '请输入单词', trigger: 'blur' }],
  partOfSpeech: [{ required: true, message: '请选择词性', trigger: 'change' }],
  wordExplain: [{ required: true, message: '请输入释义', trigger: 'blur' }]
};

// 表单引用（用于验证和重置）
const wordFormRef = ref<InstanceType<typeof ElForm>>();

// 提交表单
const submitForm = async () => {
  if (!wordFormRef.value) return;
  // 表单验证
  await wordFormRef.value.validate();

  try {
    // 调用后端增加单词接口（POST请求）
    const res = await request.post('/words', wordForm);
    ElMessage.success('单词添加成功！');
    // 重置表单
    resetForm();
  } catch (error) {
    ElMessage.error('添加失败，请重试');
    console.error('添加单词错误：', error);
  }
};

// 重置表单
const resetForm = () => {
  wordFormRef.value?.resetFields();
};
</script>

<style scoped>
.add-word-container {
  width: 600px;
  margin: 50px auto;
  padding: 20px;
}
</style>