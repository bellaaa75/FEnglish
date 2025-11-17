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
      <el-table-column label="发布时间">
        <template #default="scope">
          {{ formatDate(scope.row.publishTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.bookId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页控件 -->
    <el-pagination
      background
      :current-page="pageInfo.currentPage"
      :page-sizes="[5, 10, 15, 20]"
      :page-size="pageInfo.pageSize"
      :total="pageInfo.total"
      layout="sizes, prev, pager, next, jumper, total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 20px; text-align: right;"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import vocabularyBookService from '@/services/vocabularyBookService'
import { useRouter } from 'vue-router'

const router = useRouter()

// 搜索关键词
const searchKeyword = ref('')
// 单词书列表数据
const bookList = ref([])
// 加载状态
const loading = ref(false)
//声明pageInfo
const pageInfo = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 页面加载时获取单词书列表
onMounted(() => {
  fetchBookList()
})

// 获取单词书列表
const fetchBookList = async () => {
  try {
    loading.value = true
    console.log('请求参数：', {
      keyword: '',
      page: pageInfo.value.currentPage,
      size: pageInfo.value.pageSize
    })
    // 调用搜索接口（空关键词查全部）
    const response = await vocabularyBookService.searchBooks(
      '', 
      pageInfo.value.currentPage, 
      pageInfo.value.pageSize
    )
    console.log('接口返回数据：', response) // 确认拿到data.list和data.total
    
    // 直接解析后端返回的data字段（完全匹配你的响应格式）
    bookList.value = response.data.list || [] // 后端data.list
    pageInfo.value.total = response.data.total || 0 // 后端data.total
    
    console.log('解析后列表：', bookList.value)
    console.log('总条数：', pageInfo.value.total)
  } catch (error) {
    console.error('获取单词书列表失败详情：', error)
    ElMessage.error('获取单词书列表失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 修复：定义searchBooks函数（之前未定义）
const searchBooks = async () => {
  try {
    loading.value = true
    const response = await vocabularyBookService.searchBooks(
      searchKeyword.value,
      pageInfo.value.currentPage,
      pageInfo.value.pageSize
    )
    bookList.value = response.data.list || []
    pageInfo.value.total = response.data.total || 0
  } catch (error) {
    console.error('搜索单词书失败：', error)
    ElMessage.error('搜索失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 搜索单词书
const handleSearch = () => {
  pageInfo.value.currentPage = 1 // 搜索重置到第一页
  searchBooks() // 调用上面定义的searchBooks
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
    await ElMessageBox.confirm(
      '确定要删除这本单词书吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await vocabularyBookService.deleteBook(bookId)
    ElMessage.success('删除成功')
    fetchBookList() // 重新加载列表
  } catch (error) {
    if (error === 'cancel') return // 用户取消操作
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageInfo.value.pageSize = size
  searchBooks() // 调用定义好的searchBooks
}

// 当前页改变
const handleCurrentChange = (page) => {
  pageInfo.value.currentPage = page
  searchBooks() // 调用定义好的searchBooks
}

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