import request from '@/utils/request'

export default {
  // 添加单词
  addWord: (wordData) => request.post('/api/words', wordData),
  // 接受可选查询参数：{ page, currentPage, pageNo, pageSize, keyword, ... }
  getWordList: (params = {}) => request.get('/api/words', { params }),
  getWordById: (id) => request.get(`/api/words/${id}`),
  updateWord: (id, wordData) => request.put(`/api/words/${id}`, wordData),
  deleteWord: (id) => request.delete(`/api/words/${id}`),
}