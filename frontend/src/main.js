// src/main.js
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/global.css'

// 新增：解决 ResizeObserver 循环警告
const debounce = (fn, delay = 16) => {
  let timer = null
  return (...args) => {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), delay)
  }
}

const originalResizeObserver = window.ResizeObserver
window.ResizeObserver = class ResizeObserver extends originalResizeObserver {
  constructor(callback) {
    super(debounce(callback))
  }
}

// 初始化应用
createApp(App)
  .use(store)
  .use(router)
  .use(ElementPlus)
  .mount('#app')