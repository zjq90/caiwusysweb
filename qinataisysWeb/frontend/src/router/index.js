import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'records',
        name: 'Records',
        component: () => import('@/views/records/index.vue'),
        meta: { title: '收支明细', requiresAuth: true }
      },
      {
        path: 'records/add',
        name: 'RecordAdd',
        component: () => import('@/views/records/edit.vue'),
        meta: { title: '添加记账', requiresAuth: true }
      },
      {
        path: 'records/edit/:id',
        name: 'RecordEdit',
        component: () => import('@/views/records/edit.vue'),
        meta: { title: '编辑记账', requiresAuth: true }
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('@/views/categories/index.vue'),
        meta: { title: '收支类别', requiresAuth: true }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/index.vue'),
        meta: { title: '财务统计', requiresAuth: true }
      },
      {
        path: 'analysis',
        name: 'Analysis',
        component: () => import('@/views/analysis/index.vue'),
        meta: { title: '财务分析', requiresAuth: true }
      },
      {
        path: 'budgets',
        name: 'Budgets',
        component: () => import('@/views/budgets/index.vue'),
        meta: { title: '财务预算', requiresAuth: true }
      },
      {
        path: 'wishes',
        name: 'Wishes',
        component: () => import('@/views/wishes/index.vue'),
        meta: { title: '心愿单', requiresAuth: true }
      },
      {
        path: 'memos',
        name: 'Memos',
        component: () => import('@/views/memos/index.vue'),
        meta: { title: '备忘录', requiresAuth: true }
      },
      {
        path: 'news',
        name: 'News',
        component: () => import('@/views/news/index.vue'),
        meta: { title: '财务新闻', requiresAuth: false }
      },
      {
        path: 'news/:id',
        name: 'NewsDetail',
        component: () => import('@/views/news/detail.vue'),
        meta: { title: '新闻详情', requiresAuth: false }
      },
      {
        path: 'calculator',
        name: 'Calculator',
        component: () => import('@/views/calculator/index.vue'),
        meta: { title: '计算器', requiresAuth: false }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 财务前台管理系统` : '财务前台管理系统'
  
  const userStore = useUserStore()
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
