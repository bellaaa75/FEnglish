import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'

// 配置Axios默认请求头（JWT令牌）
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 全局挂载Axios
const app = createApp(App)
app.use(store)
app.use(router)
app.use(ElementPlus)

app.config.globalProperties.$axios = axios

app.mount('#app')
