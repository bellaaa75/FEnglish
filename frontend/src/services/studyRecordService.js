import request from '@/utils/request'
import { format } from 'date-fns'

export default {
  // 新增学习记录：payload 包含 { user: { userId }, englishWord: { wordId }, studyTime }
  addStudyRecord: (userId, wordId, studyTime = new Date()) => {
    console.log(`[studyRecordService] addStudyRecord: userId=${userId}, wordId=${wordId}, time=${studyTime}`)
    // 后端期望的时间格式为 yyyy-MM-dd HH:mm:ss
    const fmt = typeof studyTime === 'string' ? studyTime : format(studyTime, 'yyyy-MM-dd HH:mm:ss')
    // 包含多种可能的键名以兼容后端实体反序列化（兼容 getWordID / getWordId / 字段名）
    const payload = {
      user: { userId, userID: userId },
      englishWord: { wordId, wordID: wordId },
      studyTime: fmt
    }
    return request.post('/api/study-records', payload)
      .then(res => {
        console.log('[studyRecordService] addStudyRecord response:', res)
        return res
      })
      .catch(err => {
        console.error('[studyRecordService] addStudyRecord error:', err)
        throw err
      })
  }
}
