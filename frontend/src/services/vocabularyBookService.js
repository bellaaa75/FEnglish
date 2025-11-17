import request from '@/utils/request'

export default {
  // 获取单词书列表
  getBookList: (page = 1, size = 10) => {
  // 无关键词时，传递空字符串触发全量分页查询
    return request.get(`/api/vocabulary-books/search?name=&page=${page}&size=${size}`);
  },
searchBooks: (keyword = '', page = 1, size = 10) => request.get('/api/vocabulary-books/search', {
  params: {
    name: keyword,  // 对应后端@RequestParam("name")
    page: page,     // 对应后端@RequestParam("page")
    size: size      // 对应后端@RequestParam("size")
  }
}),
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
  updateBook: (bookId, bookData) => {
  return request.put(`/api/vocabulary-books/${bookId}`, bookData)}
}