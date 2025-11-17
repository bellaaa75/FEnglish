import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1. 创建实例
const request = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || 'http://localhost:8080',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// 2. 请求拦截器：统一携带 user-id 和 token
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

// 3. 响应拦截器：兼容两种后端响应格式（通过 ESLint 检查）
request.interceptors.response.use(
  response => {
    const res = response.data
    // 兼容逻辑：用 Object.prototype.hasOwnProperty.call 替代 res.hasOwnProperty（符合 ESLint 规则）
    // 情况1：登录/注册接口（返回 success 字段）
    if (Object.prototype.hasOwnProperty.call(res, 'success')) {
      if (res.success === true) {
        return res; // 登录成功，返回 token、userId 等数据
      } else {
        ElMessage.error(res.message || '业务异常')
        const err = new Error(res.message)
        err.code = res.code || -1
        throw err
      }
    }
    // 情况2：单词书/其他接口（返回 code 字段）
    else if (Object.prototype.hasOwnProperty.call(res, 'code')) {
      if (res.code === 200) {
        return res; // 业务成功，返回 data.list 等数据
      } else {
        ElMessage.error(res.message || '业务异常')
        const err = new Error(res.message)
        err.code = res.code
        throw err
      }
    }
    // 情况3：其他未知格式（直接返回，避免拦截）
    else {
      return res;
    }
  },
  error => {
    // 网络错误/HTTP错误（404、401、500等）
    console.error('请求错误详情:', error); 
    // 401 认证过期处理
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      // 清除本地存储，跳转登录页（需要的话解开注释）
      // router.push('/login')
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
    }
    // 统一错误提示
    const msg = error.response?.data?.message || error.message || '网络异常'
    ElMessage.error(msg)
    return Promise.reject(new Error(msg)); 
  }
)

export default request