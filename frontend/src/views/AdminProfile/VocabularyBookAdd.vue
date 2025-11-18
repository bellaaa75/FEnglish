<template>
  <div class="vocab-book-form">
    <el-page-header @back="handleBack" />
    
    <el-form 
      ref="bookForm" 
      :model="bookForm" 
      :rules="rules" 
      label-width="120px" 
      class="form-container"
    >
      <!-- 新增单词书ID输入框 -->
      <el-form-item label="单词书ID" prop="bookId">
        <el-input 
          v-model="bookForm.bookId" 
          placeholder="请输入单词书ID（留空则自动生成）" 
          maxlength="50"
        />
        <el-form-item__help>单词书ID由字母、数字或下划线组成，不超过50个字符</el-form-item__help>
      </el-form-item>
      
      <el-form-item label="单词书名称" prop="bookName">
        <el-input 
          v-model="bookForm.bookName" 
          placeholder="请输入单词书名称" 
          maxlength="100"
        />
      </el-form-item>
      
      <el-form-item label="发布时间" prop="publishTime">
        <el-date-picker
          v-model="bookForm.publishTime"
          type="datetime"
          placeholder="选择发布时间（留空则默认当前时间）"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
        <el-button @click="handleBack">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import vocabularyBookService from '@/services/vocabularyBookService'

const router = useRouter()
const bookForm = reactive({
  bookId: '',       // 新增：单词书ID（可选）
  bookName: '',     // 单词书名称（必填）
  publishTime: ''   // 发布时间（可选，后端会默认当前时间）
})

// 表单验证规则
const rules = {
  bookId: [
    { max: 50, message: 'ID长度不能超过50个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]*$/, message: 'ID只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  bookName: [
    { required: true, message: '请输入单词书名称', trigger: 'blur' },
    { max: 100, message: '名称长度不能超过100个字符', trigger: 'blur' }
  ]
}

const bookFormRef = ref(null)

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await bookFormRef.value.validate();
    
    // 提交数据到后端
    await vocabularyBookService.addBook(bookForm);
    
    ElMessage.success('新增成功');
    // 跳回单词书管理页面
    router.push('/profile/admin/vocabulary-books');
  } catch (error) {
    // 处理错误：如果是验证失败不显示错误提示，其他情况显示
    if (error.name !== 'ValidationError') {
      ElMessage.error('新增失败：' + (error.message || '未知错误'))
    }
  }
};

// 返回列表页
const handleBack = () => {
  router.back()
}
</script>

<style scoped>
.form-container {
  margin-top: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}
</style>