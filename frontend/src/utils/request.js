import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1. 创建实例
const request = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || 'http://localhost:8080',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// 2. 请求拦截器：统一携带 user-id（后端 @RequestHeader("user-id")）
request.interceptors.request.use(
  config => {
    const uid = localStorage.getItem('userId')
    if (uid) config.headers['userId'] = uid
    
    const token = localStorage.getItem('token');

    // 定义不需要 token 的接口路径
    const noAuthPaths = ['/user/register', '/admin/register'];
    
    // 如果 token 存在，并且请求的 URL 不在不需要 token 的列表中，才添加 token
    if (token && !noAuthPaths.some(path => config.url.includes(path))) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config;
  },
  error => Promise.reject(error)
)

// 3. 响应拦截器：统一解析 ApiResponse
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.success === true) {
      return res // 业务成功，返回数据
    } else {
      // 其它业务错误码：使用后端返回的 message 提示
      ElMessage.error(res.message || '业务异常')
      // 抛出自定义错误，调用处可用 try/catch 捕获
      const err = new Error(res.message)
      err.code = res.code // 携带后端错误码，可做特殊处理
      throw err
    }
  },
  error => {
    // 处理认证失败情况
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      // 可以在这里跳转到登录页
      /* router.push('/login') */
      // 清除本地存储的认证信息
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
    }
    const msg = error.response?.data?.message || error.message || '网络异常'
    ElMessage.error(msg)
    throw error
  }
)

export default request