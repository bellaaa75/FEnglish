import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/global.css'
/* import axios from 'axios' */


const app = createApp(App)
app.use(store)
app.use(router)
app.use(ElementPlus)

/* app.config.globalProperties.$axios = axios */ // 全局挂载Axios

app.mount('#app')
