import { createRouter, createWebHistory } from 'vue-router'
import PortalLayout from '@/layout/PortalLayout.vue'

const routes = [
  {
    path: '/',
    component: PortalLayout,
    children: [
      { path: '', name: 'home', component: () => import('@/views/Home.vue') },
      { path: 'product/list', name: 'productList', component: () => import('@/views/ProductList.vue') },
      { path: 'product/detail/:id', name: 'productDetail', component: () => import('@/views/ProductDetail.vue') },
      { path: 'cart', name: 'cart', component: () => import('@/views/Cart.vue'), meta: { requiresAuth: true } },
      { path: 'order/confirm', name: 'orderConfirm', component: () => import('@/views/OrderConfirm.vue'), meta: { requiresAuth: true } },
      { path: 'order/pay/:id', name: 'pay', component: () => import('@/views/Pay.vue'), meta: { requiresAuth: true } },
      { path: 'order/list', name: 'orderList', component: () => import('@/views/OrderList.vue'), meta: { requiresAuth: true } },
      { path: 'user/profile', name: 'profile', component: () => import('@/views/Profile.vue'), meta: { requiresAuth: true } },
      { path: 'user/address', name: 'address', component: () => import('@/views/Address.vue'), meta: { requiresAuth: true } },
      { path: 'after-sale', name: 'afterSale', component: () => import('@/views/AfterSale.vue'), meta: { requiresAuth: true } },
      { path: 'chat', name: 'chat', component: () => import('@/views/Chat.vue'), meta: { requiresAuth: true } },
      { path: 'seckill', name: 'seckill', component: () => import('@/views/Seckill.vue') }
    ]
  },
  { path: '/login', name: 'login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'register', component: () => import('@/views/Register.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 登录守卫
router.beforeEach((to) => {
  const token = sessionStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
