import { createRouter, createWebHistory } from 'vue-router'

// 用 "懒加载" 方式引入组件（推荐，避免空白）
const UserLogin = () => import('../views/UserLogin.vue')
const UserRegister = () => import('../views/UserRegister.vue')
const AdminLogin = () => import('../views/AdminLogin.vue')
const AdminRegister = () => import('../views/AdminRegister.vue')


const routes = [
  {
    path: '/',
    redirect: '/user/login' 
  },
  {
    path: '/user/login',
    name: 'UserLogin',
    component: UserLogin,
    meta: { guest: true }
  },
  {
    path: '/user/register',
    name: 'UserRegister',
    component: UserRegister,
    meta: { guest: true }
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: AdminLogin,
    meta: { guest: true }
  },
  {
    path: '/admin/register',
    name: 'AdminRegister',
    component: AdminRegister,
    meta: { guest: true }
  },
  // 404 路由（避免路由不匹配导致空白）
  {
    path: '/:pathMatch(.*)*',
    redirect: '/user/login'
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL || '/'), // 注意：process.env.BASE_URL 不能错
  routes
})

// 路由守卫不要写错（之前的代码没问题，但再检查一次）
router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('token')
  console.log('路由守卫:', to.path, '已认证:', isAuthenticated)
  
  // 如果路由需要访客权限但用户已登录，跳转到首页
  if (to.meta.guest && isAuthenticated) {
    next('/home') // 跳转到已登录用户的首页
  } 
  // 如果路由需要认证但用户未登录，跳转到登录页
  else if (to.meta.requiresAuth && !isAuthenticated) {
    next('/user/login')
  } 
  else {
    next() // 正常放行
  }
})

export default router