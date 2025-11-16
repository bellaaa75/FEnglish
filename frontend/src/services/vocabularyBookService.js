import request from '@/utils/request'

export default {
  // 获取单词书列表
  getBookList: () => request.get('/api/vocabulary-books'),
  // 搜索单词书
  searchBooks: (keyword) => request.get(`/api/vocabulary-books/search?name=${keyword}`),
  // 删除单词书
  deleteBook: (bookId) => request.delete(`/api/vocabulary-books/${bookId}`),
  // 新增单词书
  addBook: (bookData) => request({
    url: '/api/vocabulary-books',
    method: 'post',
    data: bookData,
  }),
  // 获取单词书详情
  getBookDetail: (bookId) => request.get(`/api/vocabulary-books/${bookId}`),
  // 更新单词书
  updateBook: (bookId, bookData) => request.put(`/api/vocabulary-books/${bookId}`, bookData)
}