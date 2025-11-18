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
const CollectWord = () => import('@/views/IndexProfile/CollectWord.vue');
const CollectBook = () => import('@/views/IndexProfile/CollectBook.vue');
const AdminChangePassword = () => import('../views/AdminProfile/AdminChangePassword.vue')
const DeleteAdmin = () => import('../views/AdminProfile/DeleteAdmin.vue')
const VocabularyBookManage = () => import('../views/admin/VocabularyBookManage.vue')
const BookDetailPlaza = () => import('@/views/IndexProfile/BookDetailPlaza.vue')
const BookDetailAdmin = () => import('@/views/AdminProfile/BookDetailAdmin.vue')
const LearningPlaza = () => import('@/views/IndexProfile/LearningPlaza.vue') // 单独提取，方便嵌套子路由
const StudyRecord = () => import('@/views/IndexProfile/StudyRecord.vue')
const LearnWord = () => import('@/views/IndexProfile/LearnWord.vue')
const WordDetail = () => import('@/views/IndexProfile/WordDetail.vue')

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
        path: 'userinfo', // 相对路径
        name: 'UserInfo',
        component: UserInfo,
        meta: { requiresAuth: true, title: '我的账号' }
      },
      {
        path: 'infoedit', // 相对路径
        name: 'InfoEdit',
        component: InfoEdit,
        meta: { requiresAuth: true, title: '我的账号' }
      },
      {
        path: 'changepassword', // 相对路径
        name: 'ChangePassword',
        component: ChangePassword,
        meta: { requiresAuth: true, title: '我的账号' }
      },
      {
        path: 'deleteuser', // 相对路径
        name: 'DeleteUser',
        component: DeleteUser,
        meta: { requiresAuth: true, title: '我的账号' }
      },
      {
        path: 'collect-word',
        name: 'CollectWord',
        component: CollectWord,
        meta: { title: '收藏单词' }
      },
      {
        path: 'collect-book',
        name: 'CollectBook',
        component: CollectBook,
        meta: { title: '我的单词书' }
      },
      // 学习广场路由（嵌套子路由，用于放置 BookDetail）
      {
        path: 'plaza',
        name: 'LearningPlaza',
        component: LearningPlaza,
        meta: { title: '学习广场' }
      },
      // 单词书详情页（可被广场和管理员页面共用）
      {
        path: '/profile/plaza/book-detail/:bookId',
        name: 'BookDetailPlaza',
        component: BookDetailPlaza,
        meta: { title: '单词书详情（广场）' }
      },

      // 学习记录路由
      {
        path: 'record',
        name: 'StudyRecord',
        component: StudyRecord,
        meta: { title: '学习记录' }
      },
      {
        path: 'learn-word/:wordId',
        name: 'LearnWord',
        component: LearnWord,
        meta: { title: '学习单词', requiresAuth: true }
      },
      // 单词详情页
      {
        path: 'word-detail/:wordId',
        name: 'WordDetail',
        component: WordDetail,
        meta: { title: '单词详情', requiresAuth: true }
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
      {
        path: 'userbyadmin', // 相对路径
        name: 'AdminUserByAdmin',
        component: AdminUserByAdmin,
        meta: { title: '用户信息' }
      },
      {
        path: 'changepassword', // 相对路径
        name: 'AdminChangePassword',
        component: AdminChangePassword,
        meta: { title: '修改密码' }
      },
      {
        path: 'deleteadmin', // 相对路径
        name: 'DeleteAdmin',
        component: DeleteAdmin,
        meta: { title: '注销账号' }
      },
      {
        path: 'wordlist',
        name: 'AdminWordList',
        component: () => import('../views/AdminProfile/WordList.vue'),
        meta: { title: '单词管理' }
      },
      {
        path: 'wordedit/:wordId',
        name: 'AdminWordEdit',
        component: () => import('@/views/AdminProfile/WordEdit.vue'),
        meta: { title: '编辑单词' }
      },
      {
        path: 'wordadd',
        name: 'AdminWordAdd',
        component: () => import('../views/AdminProfile/WordAdd.vue'),
        meta: { title: '添加单词' }
      },
      {
        path: 'vocabulary-books/book-detail/:bookId',
        name: 'BookDetailAdmin',
        component: BookDetailAdmin,
        meta: { title: '单词书详情（管理）', requiresAuth: true }
      }
    ]
  },

  // 404 路由（避免路由不匹配导致空白）
  {
    path: '/:pathMatch(.*)*',
    redirect: '/user/login'
  },

  // 单词书相关路由（管理员）
  {
    path: '/admin',
    component: AdminProfile, // 包含侧边栏的布局组件
    children: [
      {
        path: 'vocabulary-books',
        name: 'VocabularyBookManage',
        component: VocabularyBookManage,
        meta: { title: '单词书管理' }
      },
      {
        path: 'vocabulary-books/add',
        name: 'VocabularyBookAdd',
        component: () => import('../views/admin/VocabularyBookAdd.vue'),
        meta: { title: '新增单词书' }
      },
      {
        path: 'vocabulary-books/edit/:bookId',
        name: 'VocabularyBookEdit',
        component: () => import('../views/admin/VocabularyBookEdit.vue'),
        meta: { title: '编辑单词书' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL || '/'), // 保持原有配置
  routes
})

// 路由守卫（保持不变）
router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('token')
  console.log('路由守卫:', to.path, '已认证:', isAuthenticated)
  
  if (to.meta.guest && isAuthenticated) {
    router.push({ name: 'IndexProfile' })
  } else if (to.meta.requiresAuth && !isAuthenticated) {
    next('/user/login')
  } else {
    next()
  }
})

export default router