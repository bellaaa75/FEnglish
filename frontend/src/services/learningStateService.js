import request from '@/utils/request'

export default {
  // 查询单个用户某个单词的学习状态
  getLearningState: (userId, wordId) => {
    console.log(`[learningStateService] getLearningState: userId=${userId}, wordId=${wordId}`);
    return request.get('/api/learning-states', { params: { userId, wordId } })
      .then(res => {
        console.log('[learningStateService] getLearningState response:', res);
        return res;
      })
      .catch(err => {
        console.error('[learningStateService] getLearningState error:', err);
        throw err;
      });
  },
  // 新增（默认状态为 未学）
  addLearningState: (userId, wordId) => {
    console.log(`[learningStateService] addLearningState: userId=${userId}, wordId=${wordId}`);
    return request({
      url: '/api/learning-states',
      method: 'post',
      params: { userId, wordId }
    })
      .then(res => {
        console.log('[learningStateService] addLearningState response:', res);
        return res;
      })
      .catch(err => {
        console.error('[learningStateService] addLearningState error:', err);
        throw err;
      });
  },
  // 修改为指定的状态（state 值为字符串：'未学'|'已学'|'熟练掌握'）
  updateLearningState: (userId, wordId, state) => {
    console.log(`[learningStateService] updateLearningState: userId=${userId}, wordId=${wordId}, state=${state}`);
    return request({
      url: '/api/learning-states',
      method: 'put',
      params: { userId, wordId, state }
    })
      .then(res => {
        console.log('[learningStateService] updateLearningState response:', res);
        return res;
      })
      .catch(err => {
        console.error('[learningStateService] updateLearningState error:', err);
        throw err;
      });
  }
}
