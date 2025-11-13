import axios from 'axios'
const state = {
  userID: JSON.parse(localStorage.getItem('userID')) || null,
  token: localStorage.getItem('token') || null,
  isAuthenticated: !!localStorage.getItem('token'),
  loading: false,
  error: null
}

const mutations = {
  AUTH_REQUEST(state) {
    state.loading = true
    state.error = null
  },
  AUTH_SUCCESS(state, { userID, token, isAdmin }) {
    state.loading = false
    state.isAuthenticated = true
    state.userID = userID
    state.token = token
    localStorage.setItem('userID', JSON.stringify(userID))
    localStorage.setItem('token', token)
    localStorage.setItem('isAdmin', isAdmin) // 标记是否为管理员
  },
  AUTH_FAILURE(state, error) {
    state.loading = false
    state.error = error
  },
  LOGOUT(state) {
    state.user = null
    state.token = null
    state.isAuthenticated = false
    localStorage.removeItem('userID')
    localStorage.removeItem('token')
    localStorage.removeItem('isAdmin')
  }
}

const actions = {
  // 普通用户登录
  
  async userLogin({ commit }, credentials) {
    try {
      commit('AUTH_REQUEST')
      console.log('发送登录请求:', credentials)
      const res = await axios.post('/api/user/login', credentials)
      console.log('登录响应:', res.data)
      commit('AUTH_SUCCESS', { 
        userID: res.data.userID, 
        token: res.data.token, 
        isAdmin: false 
      })
      return res.data.userID
    } catch (error) {
      console.error('Login error:', error); 
      const errorMsg = error.response?.data?.message || '登录失败'
      commit('AUTH_FAILURE', errorMsg)
      throw new Error(errorMsg) 
    }
  },

  // 普通用户注册
  async userRegister({ commit }, userData) {
    try {
      commit('AUTH_REQUEST')
      const res = await axios.post('/api/user/register', userData)
      commit('AUTH_SUCCESS', { 
        userID: res.data.userID, 
        token: res.data.token, 
        isAdmin: false 
      })
      return res.data.userID
    } catch (error) {
      commit('AUTH_FAILURE', error.response?.data?.message || '注册失败')
      throw error
    }
  },

  // 管理员登录
  async adminLogin({ commit }, credentials) {
    try {
      commit('AUTH_REQUEST')
      const res = await axios.post('/api/admin/login', credentials)
      commit('AUTH_SUCCESS', { 
        userID: res.data.userID, 
        token: res.data.token, 
        isAdmin: true 
      })
      return res.data.userID
    } catch (error) {
      commit('AUTH_FAILURE', error.response?.data?.message || '管理员登录失败')
      throw error
    }
  },

  // 管理员注册
  async adminRegister({ commit }, userData) {
    try {
      commit('AUTH_REQUEST')
      const res = await axios.post('/api/admin/register', userData)
      commit('AUTH_SUCCESS', { 
        userID: res.data.userID, 
        token: res.data.token, 
        isAdmin: true 
      })
      return res.data.userID
    } catch (error) {
      commit('AUTH_FAILURE', error.response?.data?.message || '管理员注册失败')
      throw error
    }
  },

  // 登出
  logout({ commit }) {
    commit('LOGOUT')
  }
}

const getters = {
  isAuthenticated: state => state.isAuthenticated,
  currentUser: state => state.userID,
  isAdmin: () => localStorage.getItem('isAdmin') === 'true',
  authLoading: state => state.loading,
  authError: state => state.error
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}