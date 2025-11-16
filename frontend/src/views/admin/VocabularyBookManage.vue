<template>
  <div class="vocab-book-manage">
    <!-- 操作栏：搜索框 + 添加按钮 -->
    <div class="operation-bar">
      <el-input 
        v-model="searchKeyword" 
        placeholder="搜索单词书名称" 
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增单词书
      </el-button>
    </div>

    <!-- 单词书列表 -->
    <el-table 
      :data="bookList" 
      border 
      style="width: 100%; margin-top: 20px"
    >
      <el-table-column prop="bookId" label="ID" width="180" />
      <el-table-column prop="bookName" label="单词书名称" width="200" />
      <el-table-column prop="publishTime" label="发布时间" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.bookId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import vocabularyBookService from '@/services/vocabularyBookService' // 需要创建该服务

// 搜索关键词
const searchKeyword = ref('')
// 单词书列表数据
const bookList = ref([])

// 页面加载时获取单词书列表
onMounted(() => {
  fetchBookList()
})

// 获取单词书列表
const fetchBookList = async () => {
  try {
    const response = await vocabularyBookService.getBookList()
    bookList.value = response.data
  } catch (error) {
    console.error('获取单词书列表失败:', error)
  }
}

// 搜索单词书
const handleSearch = () => {
  // 实现搜索逻辑，调用后端接口
  if (searchKeyword.value) {
    vocabularyBookService.searchBooks(searchKeyword.value).then(res => {
      bookList.value = res.data
    })
  } else {
    fetchBookList() // 空关键词时显示全部
  }
}

// 新增单词书
const handleAdd = () => {
  router.push('/profile/admin/vocabulary-books/add')
}

// 编辑单词书
const handleEdit = (book) => {
  router.push(`/profile/admin/vocabulary-books/edit/${book.bookId}`)
}

// 删除单词书
const handleDelete = async (bookId) => {
  try {
    await vocabularyBookService.deleteBook(bookId)
    fetchBookList() // 重新加载列表
  } catch (error) {
    console.error('删除失败:', error)
  }
}

import { useRouter } from 'vue-router'
const router = useRouter()

</script>

<style scoped>
.vocab-book-manage {
  padding: 20px;
  background-color: #fff;
  min-height: 100vh;
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-input {
  width: 300px;
}
</style>