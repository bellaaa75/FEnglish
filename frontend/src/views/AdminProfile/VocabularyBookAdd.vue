<template>
  <div class="vocab-book-form">
    <el-page-header @back="handleBack" />
    
    <el-form 
      ref="bookFormRef"
      :model="bookForm" 
      :rules="formRules"
      label-width="120px" 
      class="form-container"
    >
      <!-- 单词书ID输入框 -->
      <el-form-item label="单词书ID" prop="bookId">
        <el-input 
          v-model.trim="bookForm.bookId"
          placeholder="请输入单词书ID（留空则自动生成）" 
          maxlength="50"
          type="text"
        />
        <el-form-item__help>单词书ID由字母、数字或下划线组成，不超过50个字符</el-form-item__help>
      </el-form-item>
      
      <!-- 单词书名称输入框 -->
      <el-form-item label="单词书名称" prop="bookName">
        <el-input 
          v-model.trim="bookForm.bookName"
          placeholder="请输入单词书名称" 
          maxlength="100"
          type="text"
        />
      </el-form-item>
      
      <!-- 发布时间选择器 -->
      <el-form-item label="发布时间" prop="publishTime">
        <el-date-picker
          v-model="bookForm.publishTime"
          type="datetime"
          placeholder="选择发布时间（留空则默认当前时间）"
          value-format="YYYY-MM-DDTHH:mm:ss"
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

// 响应式表单数据
const bookForm = reactive({
  bookId: '',
  bookName: '',
  publishTime: ''
})

// 表单引用
const bookFormRef = ref(null)

// 表单验证规则 - 重命名避免"未使用"错误，确保触发方式合理
const formRules = {
  bookId: [
    { max: 50, message: 'ID长度不能超过50个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]*$/, message: 'ID只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  bookName: [
    { required: true, message: '请输入单词书名称', trigger: ['blur', 'change'] },
    { max: 100, message: '名称长度不能超过100个字符', trigger: 'blur' }
  ]
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await bookFormRef.value.validate()
    
    // 处理空值（后端友好格式）
    const submitData = {
      bookId: bookForm.bookId || undefined,
      bookName: bookForm.bookName.trim(),
      publishTime: bookForm.publishTime || undefined
    }
    
    // 调用后端接口
    await vocabularyBookService.addBook(submitData)
    
    ElMessage.success('新增单词书成功！')
    router.push('/profile/admin/vocabulary-books')
  } catch (error) {
    if (error.name !== 'ValidationError') {
      ElMessage.error(`新增失败：${error.message || '服务器错误'}`)
    }
  }
}

// 返回上一页
const handleBack = () => {
  router.back()
}
</script>

<style scoped>
.form-container {
  margin-top: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/*使用Vue3推荐的深度选择器 */
:deep(.el-input__inner) {
  width: 100%;
  box-sizing: border-box;
}

.el-form-item {
  margin-bottom: 24px;
}

/* 优化输入框聚焦样式 */
:deep(.el-input:focus-within .el-input__inner) {
  border-color: #1989fa;
  box-shadow: 0 0 0 2px rgba(25, 137, 250, 0.2);
}
</style>