import { createRouter, createWebHistory } from 'vue-router';

const WordList = () => import('../views/word/WordList.vue');
const AddWord = () => import('../views/word/AddWord.vue');


// 先创建空页面占位，后续完善
const Login = () => import('../views/Login.vue');
const Home = () => import('../views/Home.vue');

const routes = [
  { path: '/login', component: Login, name: '登录页' },
  { path: '/', component: Home, name: '首页' },
  { path: '/:pathMatch(.*)*', redirect: '/login' } // 404页面重定向到登录
  { 
    path: '/words', 
    component: WordList, 
    name: '单词列表',
    meta: { requiresAuth: true } // 标记需要登录才能访问
  },
  { 
    path: '/words/add', 
    component: AddWord, 
    name: '新增单词',
    meta: { requiresAuth: true }
  }
];

export default createRouter({
  history: createWebHistory(),
  routes
});