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
const AdminChangePassword = () => import('../views/AdminProfile/AdminChangePassword.vue')
const DeleteAdmin = () => import('../views/AdminProfile/DeleteAdmin.vue')
const VocabularyBookManage = () => import('../views/admin/VocabularyBookManage.vue')


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
      },
      // 学习广场路由
      {
        path: 'plaza',
        name: 'LearningPlaza',  // 同步修改名称
        component: () => import('../views/IndexProfile/LearningPlaza.vue')  // 修正路径
      },
      // 学习记录路由
      {
        path: 'record',
        name: 'StudyRecord',  // 同步修改名称
        component: () => import('../views/IndexProfile/StudyRecord.vue')  // 修正路径
      },
      {
        path: 'learn-word/:wordId',
        name: 'LearnWord',
        component: () => import('../views/IndexProfile/LearnWord.vue'),
        meta: { title: '学习单词', requiresAuth: true }
      },
      // 单词详情页（复用或新建）
      {
        path: 'word-detail/:wordId',
        name: 'WordDetail',
        component: () => import('../views/IndexProfile/WordDetail.vue'), // 需创建
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
      // 子路由
      {
        path: 'userbyadmin', //相对路径
        name: 'AdminUserByAdmin',
        component: AdminUserByAdmin,
        meta: { title: '用户信息' } // 子页面标题，用于Profile布局中显示
      },
      {
        path: 'changepassword', //相对路径
        name: 'AdminChangePassword',
        component: AdminChangePassword,
        meta: { title: '修改密码' } // 子页面标题，用于Profile布局中显示
      },
      {
        path: 'deleteadmin', //相对路径
        name: 'DeleteAdmin',
        component: DeleteAdmin,
        meta: { title: '注销账号' } // 子页面标题，用于Profile布局中显示
      },
      {
        path: 'wordlist',  // 管理员下的单词列表路径
        name: 'AdminWordList',
        component: () => import('../views/AdminProfile/WordList.vue'),  // 直接在这里写路径
        meta: { title: '单词管理' }  // 显示在页面标题
      },
      {
        path: 'wordedit/:wordId', // 动态路由参数
        name: 'AdminWordEdit',
        component: () => import('@/views/AdminProfile/WordEdit.vue'),
        meta: { title: '编辑单词' }
      },
      {
        path: 'wordadd', // 相对路径（完整路径：/profile/admin/wordadd）
        name: 'AdminWordAdd', // 路由名称，和跳转时保持一致
        component: () => import('../views/AdminProfile/WordAdd.vue'), // 组件路径（和 wordlist 保持同级）
        meta: { title: '添加单词' } // 页面标题，会显示在 Profile 布局的标题区域
      }
    ]
  },

  // 404 路由（避免路由不匹配导致空白）
  {
    path: '/:pathMatch(.*)*',
    redirect: '/user/login'
  },

  //单词书相关路由
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