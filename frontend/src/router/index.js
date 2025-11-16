/* import { pa } from 'element-plus/es/locale' */
import { createRouter, createWebHistory } from 'vue-router'

// 用 "懒加载" 方式引入组件（推荐，避免空白）
const UserLogin = () => import('../views/UserLogin.vue')
const UserRegister = () => import('../views/UserRegister.vue')
const AdminLogin = () => import('../views/AdminLogin.vue')
const AdminRegister = () => import('../views/AdminRegister.vue')
const IndexProfile = () => import('../views/IndexProfile.vue')
const UserInfo = () => import('../views/IndexProfile/UserInfo.vue')
const AdminProfile = () => import('../views/AdminProfile.vue')
const AdminUserByAdmin = () => import('../views/AdminProfile/UserByAdmin.vue')
const InfoEdit = () => import('../views/IndexProfile/InfoEdit.vue')
const ChangePassword = () => import('../views/IndexProfile/ChangePassword.vue')
const DeleteUser = () => import('../views/IndexProfile/DeleteUser.vue')
const CollectWord = () => import('@/views/collect/CollectWord.vue');
const CollectBook = () => import('@/views/collect/CollectBook.vue');
const WordAdd = () => import('../views/WordAdd.vue')
const WordList = () => import('../views/WordList.vue')


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

   // 新增：Profile布局路由（父路由）
  {
    path: '/profile',
    name: 'IndexProfile',
    component: IndexProfile,
    meta: { requiresAuth: true }, // 需要登录才能访问
    redirect: '/profile/userinfo', // 默认重定向到第一个子路由
    children: [
      // 子路由
      {
        path: 'userinfo', //相对路径
        name: 'UserInfo',
        component: UserInfo,
        meta: {requiresAuth: true, title: '我的账号' } // 子页面标题，用于Profile布局中显示
      },
      {
        path: 'infoedit', //相对路径
        name: 'InfoEdit',
        component: InfoEdit,
        meta: {requiresAuth: true, title: '我的账号' } // 子页面标题，用于Profile布局中显示
      },
      {
        path: 'changepassword', //相对路径
        name: 'ChangePassword',
        component: ChangePassword,
        meta: {requiresAuth: true, title: '我的账号' } // 子页面标题，用于Profile布局中显示
      },
      {
        path: 'deleteuser', //相对路径
        name: 'DeleteUser',
        component: DeleteUser,
        meta: {requiresAuth: true, title: '我的账号' } // 子页面标题，用于Profile布局中显示
      },
      {
        path: 'collect-word', // 添加 collect-word 子路径
        name: 'CollectWord',
        component: CollectWord,
        meta: { title: '收藏单词' } // 子页面标题，用于Profile布局中显示
      },
      {
        path: 'collect-book', // 添加 collect-word 子路径
        name: 'CollectBook',
        component: CollectBook,
        meta: { title: '我的单词书' } // 子页面标题，用于Profile布局中显示
      }

    ]
  },
  // 管理员
  {
    path: '/profile/admin',
    name: 'AdminProfile',
    component: AdminProfile,
    meta: { requiresAuth: true }, // 需要登录才能访问
    redirect: '/profile/admin/userbyadmin', // 默认重定向到第一个子路由
    children: [
      // 子路由
      {
        path: 'userbyadmin', //相对路径
        name: 'AdminUserByAdmin',
        component: AdminUserByAdmin,
        meta: { title: '用户信息' } // 子页面标题，用于Profile布局中显示
      }
    ]
  },

  {
    path: '/word/add',
    name: 'WordAdd',
    component: WordAdd,
    meta: { requiresAuth: true, isAdmin: true } // 假设只有管理员可添加
  },
  // 管理员
  {
    path: '/word/list',
    name: 'WordList',
    component: WordList,
    meta: { requiresAuth: true }
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

// 路由守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('token')
  console.log('路由守卫:', to.path, '已认证:', isAuthenticated)
  
  // 如果路由需要访客权限但用户已登录，跳转到首页
  if (to.meta.guest && isAuthenticated) {
    router.push({ name: 'IndexProfile' }) // 跳转到已登录用户的首页
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