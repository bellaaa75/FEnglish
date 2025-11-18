<template>
  <div class="vocab-book-form">
    <el-page-header :content="['单词书管理', '编辑单词书']" @back="handleBack" />
    
    <el-form 
      ref="bookForm" 
      :model="bookForm" 
      :rules="rules" 
      label-width="120px" 
      class="form-container"
    >
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
          value-format="YYYY-MM-DD HH:mm:ss"
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

const bookForm = reactive({
  bookName: '',
  publishTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
})

// 表单验证规则
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
    const bookId = route.params.bookId;
    const res = await vocabularyBookService.getBookDetail(bookId);
//  bookForm.value = res.data; // 初始化表单
    bookForm.bookName = res.data.bookName
    bookForm.publishTime = res.data.publishTime
  } catch (error) {
    ElMessage.error('获取单词书详情失败')
    console.error(error)
  }
})

// 提交修改
const handleSubmit = async () => {
  try {
    await bookFormRef.value.validate()
    await vocabularyBookService.updateBook(route.params.bookId, bookForm.value)
    ElMessage.success('修改成功')
    router.push('/profile/admin/vocabulary-books')
  } catch (error) {
    if (error.name === 'Error') {
      ElMessage.error(error.message || '修改失败')
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