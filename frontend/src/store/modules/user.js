/* import axios from 'axios' */
import request from '@/utils/request' 

const state = {
  userId: JSON.parse(localStorage.getItem('userId')) || null,
  token: localStorage.getItem('token') || null,
  isAuthenticated: !!localStorage.getItem('token'),
  isAdmin: localStorage.getItem('isAdmin') === 'true',
  loading: false,
  error: null,
  userInfo: JSON.parse(localStorage.getItem('userInfo')) || null 
}

const mutations = {
  AUTH_REQUEST(state) {
    state.loading = true
    state.error = null
  },
  AUTH_SUCCESS(state, { userId, token, isAdmin, userInfo }) {
    state.loading = false
    state.isAuthenticated = !!token
    state.userId = userId
    state.token = token
    localStorage.setItem('userId', JSON.stringify(userId))
    if (token) {
      localStorage.setItem('token', token);
    }
    localStorage.setItem('isAdmin', isAdmin) // 标记是否为管理员
    localStorage.setItem('userInfo', JSON.stringify(userInfo)) // 新增：本地存储用户信息
  },
  UPDATE_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  },
  AUTH_FAILURE(state, error) {
    state.loading = false
    state.isAuthenticated = false;
    state.userId = null;
    state.token = null;
    state.error = error
/*     localStorage.removeItem('token'); */
  },
  LOGOUT(state) {
    state.user = null
    state.token = null
    state.isAuthenticated = false
    state.userInfo = null
    localStorage.removeItem('userId')
    localStorage.removeItem('token')
    localStorage.removeItem('isAdmin')
    localStorage.removeItem('userInfo') 
  }
}

const actions = {

  //普通用户登录
  async userLogin({ commit }, credentials) {
    try {
      localStorage.removeItem('token'); 
      commit('AUTH_REQUEST')
      console.log('发送登录请求:', credentials)
      const res = await request.post('/api/user/login', credentials)
      console.log('登录响应:', res)
      commit('AUTH_SUCCESS', { 
        userId: res.userId, 
        token: res.token, 
        isAdmin: false,
        userInfo: res.userInfo || null
      })
      
      const userInfo = await this.dispatch('user/fetchUserInfo')
      console.log('用户信息响应:', userInfo) 

      return res.userID
    } catch (error) {
      console.error('Login error:', error); 
      const errorMsg = error.message || '登录失败'
      commit('AUTH_FAILURE', errorMsg)
      throw new Error(errorMsg) 
    }
  },

  // 普通用户注册
  async userRegister({ commit }, userData) {
    try {
      localStorage.removeItem('token'); 
      commit('AUTH_REQUEST')
      console.log('发送注册请求:', userData)
      const res = await request.post('/api/user/register', userData)
      console.log('注册响应:', res)
      commit('AUTH_SUCCESS', { 
        userId: res.userId, 
        token: res.token || null, 
        isAdmin: false,
        userInfo: null
      })
      return res.userId
    } catch (error) {
      commit('AUTH_FAILURE', error.message || '注册失败')
      throw error
    }
  },

  // 管理员登录
  async adminLogin({ commit }, credentials) {
    try {
      localStorage.removeItem('token'); 
      commit('AUTH_REQUEST')
      const res = await request.post('/api/admin/login', credentials)
      commit('AUTH_SUCCESS', { 
        userId: res.userId, 
        token: res.token, 
        isAdmin: true,
        userInfo: res.userInfo || null
      })
      return res.userId
    } catch (error) {
      commit('AUTH_FAILURE', error.message || '管理员登录失败')
      throw error
    }
  },

  // 管理员注册
  async adminRegister({ commit }, userData) {
    try {
      localStorage.removeItem('token'); 
      commit('AUTH_REQUEST')
      const res = await request.post('/api/admin/register', userData)
      commit('AUTH_SUCCESS', { 
        userId: res.userId, 
        token: res.token || null, 
        isAdmin: true,
        userInfo: res.userInfo || null
      })
      return res.userId
    } catch (error) {
      commit('AUTH_FAILURE', error.message || '管理员注册失败')
      throw error
    }
  },
  //获取用户信息
 async fetchUserInfo({ commit, state }) {
    try {
      commit('AUTH_REQUEST')
      // 校验：若未登录（无 token），直接抛出错误
      if (!state.token) {
        throw new Error('未登录，无法获取用户信息')
      }
      console.log('发送获取用户信息请求（携带 token）')
      // 关键：使用 GET 方法（无需请求体），token 已在 request.js 中自动携带
      const res = await request.get('/api/user/info') 
      console.log('用户信息响应:', res)

      // 校验后端响应是否成功
      if (!res.success || !res.userInfo) {
        throw new Error('获取用户信息失败')
      }

      // 调用 mutation 更新用户信息（不影响现有认证状态）
      commit('UPDATE_USER_INFO', res.userInfo)
      return res.userInfo // 返回用户信息，供组件使用
    } catch (error) {
      console.error('获取用户信息失败:', error)
      const errorMsg = error.message || '获取用户信息异常'
      commit('AUTH_FAILURE', errorMsg)
      throw new Error(errorMsg) // 抛出错误，供组件捕获处理
    }
  },

  // 登出
  logout({ commit }) {
    commit('LOGOUT')
  }
}

const getters = {
  isAuthenticated: state => state.isAuthenticated,
  currentUser: state => state.userId,
  isAdmin: () => localStorage.getItem('isAdmin') === 'true',
  authLoading: state => state.loading,
  authError: state => state.error,
  userInfo: state => state.userInfo
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}