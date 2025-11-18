<template>
  <div class="vocab-book-form">
    <el-page-header @back="handleBack" />
    
    <el-form 
      ref="bookFormRef" 
      :model="bookForm" 
      :rules="rules" 
      label-width="120px" 
      class="form-container"
    >
      <!-- 单词书ID不可编辑，仅展示 -->
      <el-form-item label="单词书ID" prop="bookId">
        <el-input v-model="bookForm.bookId" disabled />
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
          placeholder="选择发布时间"
          value-format="YYYY-MM-DDTHH:mm:ss"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
        <el-button @click="handleBack">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import vocabularyBookService from '@/services/vocabularyBookService'

const router = useRouter()
const route = useRoute()

// 表单数据
const bookForm = reactive({
  bookId: '',       // 单词书ID（不可编辑）
  bookName: '',     // 单词书名称（可编辑）
  publishTime: ''   // 发布时间（可编辑）
})

// 表单验证规则（与新增页面保持一致）
const rules = {
  bookName: [
    { required: true, message: '请输入单词书名称', trigger: 'blur' },
    { max: 100, message: '名称长度不能超过100个字符', trigger: 'blur' }
  ]
}

const bookFormRef = ref(null)

// 页面加载时获取单词书详情
onMounted(async () => {
  try {
    const bookId = route.params.bookId
    if (!bookId) {
      ElMessage.error('单词书ID不存在')
      router.back()
      return
    }

    // 调用接口获取单词书详情
    const res = await vocabularyBookService.getBookDetail(bookId)
    const bookData = res.data || {}
    
    // 填充表单数据（注意时间格式兼容性处理）
    bookForm.bookId = bookData.bookId
    bookForm.bookName = bookData.bookName
    bookForm.publishTime = bookData.publishTime 
      ? bookData.publishTime.replace('T', ' ') 
      : ''
  } catch (error) {
    ElMessage.error('获取单词书详情失败：' + (error.message || '未知错误'))
    console.error(error)
  }
})

// 提交编辑内容
const handleSubmit = async () => {
  try {
    // 表单验证
    await bookFormRef.value.validate()
    
    // 调用更新接口（传递bookId和表单数据）
    await vocabularyBookService.updateBook(route.params.bookId, {
      bookName: bookForm.bookName,
      publishTime: bookForm.publishTime || null
    })
    
    ElMessage.success('修改成功')
    router.push('/profile/admin/vocabulary-books')
  } catch (error) {
    // 区分表单验证错误和后端错误
    if (error.name !== 'ValidationError') {
      ElMessage.error('修改失败：' + (error.message || '未知错误'))
    }
  }
}

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