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
    const uid = localStorage.getItem('user-id')
    if (uid) config.headers['user-id'] = uid
    return config
  },
  error => Promise.reject(error)
)

// 3. 响应拦截器：统一解析 ApiResponse
request.interceptors.response.use(
  response => {
    const res = response.data
    // 后端约定：code === 200 才是业务成功
    if (res.code === 200) {
      return res.data // 只返回 data 部分，调用处不再 .data.data
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
    // HTTP 网络/超时/500 等
    const msg = error.response?.data?.message || error.message || '网络异常'
    ElMessage.error(msg)
    throw error
  }
)

export default request