import request from '@/utils/request'

export default {
  // 添加单词
  addWord: (wordData) => request.post('/api/words', wordData),
  // 后续可添加其他接口：查询/修改/删除等
  getWordList: () => request.get('/api/words'),
  getWordById: (id) => request.get(`/api/words/${id}`),
  updateWord: (id, wordData) => request.put(`/api/words/${id}`, wordData),
  deleteWord: (id) => request.delete(`/api/words/${id}`)
}