import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // 路由
import store from './store';   // Vuex
import ElementPlus from 'element-plus'; // UI组件库
import 'element-plus/dist/index.css'; // UI样式

createApp(App)
  .use(router)
  .use(store)
  .use(ElementPlus)
  .mount('#app');