/* import axios from 'axios' */
import request from '@/utils/request' 

const state = {
  userId: JSON.parse(localStorage.getItem('userId')) || null,
  token: localStorage.getItem('token') || null,
  isAuthenticated: !!localStorage.getItem('token'),
  isAdmin: false,
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
    /* state.isAuthenticated = false;
    state.userId = null;
    state.token = null; */
    state.error = error
/*     localStorage.removeItem('token'); */
  },
  LOGOUT(state) {
    state.userId = null
    state.token = null
    state.isAuthenticated = false
    state.userInfo = null
    state.isAdmin = false
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
      const errorMsg = error.message || '注册失败'
      commit('AUTH_FAILURE', errorMsg)
      throw new Error(errorMsg);
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
      const errorMsg = error.message || '管理员登录失败'
      commit('AUTH_FAILURE', errorMsg)
      throw new Error(errorMsg);
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
      const errorMsg = error.message || '管理员注册失败'
      commit('AUTH_FAILURE', errorMsg)
      throw new Error(errorMsg);
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
  //修改用户信息
  async updateUserInfo({ commit, state }, userData) {
    try {
      commit('AUTH_REQUEST')

      // 检查用户是否已登录
      if (!state.isAuthenticated) {
        throw new Error('用户未登录，无法更新信息')
      }

      // 发送 PUT 请求到后端接口
      const res = await request.put('/api/user/info', userData)
      console.log('更新用户信息响应:', res)

      if (res.success) {
        const userInfo = await this.dispatch('user/fetchUserInfo')
        console.log('用户信息响应:', userInfo) 
        return userInfo // 返回更新后的用户信息
      } else {
        // 如果后端返回错误信息，抛出异常
        throw new Error(res.message || '更新用户信息失败')
      }
    } catch (error) {
      // 处理错误，调用 mutation 保存错误信息
      const errorMsg = error.message || '网络错误，更新失败'
      commit('AUTH_FAILURE', errorMsg)
      throw new Error(errorMsg); // 将错误向上抛出，以便组件捕获
    }
  },
  //修改密码
  async changePassword({ commit }, { oldPassword, newPassword }) {
    try {
      commit('AUTH_REQUEST')
      // 调用后端修改密码接口
      const res = await request.put('/api/user/password', { oldPassword, newPassword })
      if (res.success) {
        console.log('密码修改成功')
        return res
      } else {
        throw new Error(res.message || '密码修改失败')
      }
    } catch (error) {
      const errorMsg = error.message || '密码修改异常'
      commit('AUTH_FAILURE', errorMsg)
      throw new Error(errorMsg);
    }
  },
  // 注销账号
  async cancelAccount({ commit, state }, password) {
    try {
      commit('AUTH_REQUEST');

      if (!state.token) {
        throw new Error('用户未登录');
      }

      // 构造要发送给后端的数据
      const requestData = {
        password: password
      };

      // 关键修正：使用 config.data 来传递请求体
      const res = await request.delete('/api/user/account', {
        data: requestData // 将数据放在 config.data 中
      });

      if (res.success) {
        commit('LOGOUT');
        return res;
      } else {
        throw new Error(res.message || '注销失败');
      }
    } catch (error) {
      console.error('注销账号错误:', error);
      
      // 错误处理逻辑
      let errorMsg = error.message || '注销异常';
      if (error.response && error.response.status === 400) {
        errorMsg = '密码错误或参数无效';
      } else if (error.response && error.response.status === 500) {
        errorMsg = '服务器内部错误，请稍后重试';
      }

      commit('AUTH_FAILURE', errorMsg);
      throw new Error(errorMsg);
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
  isAdmin: state => state.isAdmin,
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