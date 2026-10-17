import { createRouter, createWebHistory } from 'vue-router'
import ServiceLayout from '@/layout/ServiceLayout.vue'

const routes = [
  {
    path: '/',
    component: ServiceLayout,
    redirect: '/workbench',
    children: [
      { path: 'workbench', name: 'workbench', component: () => import('@/views/Workbench.vue'), meta: { title: '客服工作台' } }
    ]
  },
  { path: '/login', name: 'login', component: () => import('@/views/Login.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = sessionStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
