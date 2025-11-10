import axios from 'axios';
import store from '../store'; // 引入Vuex获取token

const request = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端API前缀
  timeout: 5000
});

// 请求拦截器：添加token
request.interceptors.request.use(config => {
  if (store.state.token) {
    config.headers.Authorization = `Bearer ${store.state.token}`;
  }
  return config;
});

// 响应拦截器：简化返回数据
request.interceptors.response.use(res => res.data);

export default request;