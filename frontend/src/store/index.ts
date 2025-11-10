import { createStore } from 'vuex';

export default createStore({
  state: {
    // 存储登录用户信息（初始为空）
    user: null,
    // 存储JWT令牌
    token: localStorage.getItem('token') || ''
  },
  mutations: {
    // 保存登录状态
    setUser(state, userInfo) {
      state.user = userInfo;
    },
    // 保存令牌
    setToken(state, token) {
      state.token = token;
      localStorage.setItem('token', token); // 持久化到本地存储
    }
  }
});