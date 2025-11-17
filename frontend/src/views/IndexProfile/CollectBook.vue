<template>
  <div class="collect-books">
    <h1>我的单词书收藏</h1>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-if="error" class="error">加载失败，请稍后再试</div>
    <div v-if="!loading && !error && books.length === 0" class="empty">
      暂无收藏的单词书
    </div>
    <ul v-if="!loading && !error && books.length > 0" class="book-list">
      <li v-for="book in books" :key="book.id" class="book-item">
        <div class="book-title">{{ book.title }}</div>
        <div class="book-description">{{ book.description }}</div>
        <el-button type="danger" @click="unCollectBook(book.id)">取消收藏</el-button>
      </li>
    </ul>
  </div>
</template>

<script>
import request from '@/utils/request'; // 假设 request.js 文件位于 src/utils/request.js
import { ElMessage } from 'element-plus';

export default {
  name: 'CollectBooks',
  data() {
    return {
      books: [],
      loading: false,
      error: false,
    };
  },
  methods: {
    async fetchBooks() {
      this.loading = true;
      this.error = false;
      console.log("Fetching books..."); // 打印方法开始执行
      // try {
      //   const response = await request.get('/api/collect/books', {
      //     params: { size: 10, page: 0 }, // 默认分页大小
      //   });
        
      //   this.books = response.content || []; // 假设后端返回的数据结构包含 content 字段
      try {
        const params = { size: 10, page: 0 }; // 明确设置分页参数
        console.log("Request Params:", params);
        const response = await request.get('/api/collect/books', { params });
        console.log("Response Data:", response);
        this.books = response.data.data?.content || [];
      } catch (err) {
        this.error = true;
        ElMessage.error(err.message || '加载失败');
      } finally {
        this.loading = false;
      }
    },
    async unCollectBook(bookId) {
      try {
        await request.delete(`/api/collect/book/${bookId}`);
        ElMessage.success('取消收藏成功');
        this.fetchBooks(); // 刷新列表
      } catch (err) {
        ElMessage.error(err.message || '取消收藏失败');
      }
    },
  },
  mounted() {
    console.log("Component mounted"); // 打印组件挂载
    this.fetchBooks();
  },
};
</script>

<style scoped>
.collect-books {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.book-list {
  list-style: none;
  padding: 0;
}
.book-item {
  background: #f9f9f9;
  border: 1px solid #ddd;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 5px;
}
.book-title {
  font-size: 18px;
  font-weight: bold;
}
.book-description {
  font-size: 14px;
  color: #666;
}
.loading {
  text-align: center;
  color: #999;
}
.error {
  text-align: center;
  color: red;
}
.empty {
  text-align: center;
  color: #666;
}
</style>