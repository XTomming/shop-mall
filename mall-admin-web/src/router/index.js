import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/layout/AdminLayout.vue'

const routes = [
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '首页统计' } },
      { path: 'product/list', name: 'productList', component: () => import('@/views/product/List.vue'), meta: { title: '商品管理' } },
      { path: 'product/edit/:id?', name: 'productEdit', component: () => import('@/views/product/Edit.vue'), meta: { title: '商品编辑' } },
      { path: 'category/list', name: 'categoryList', component: () => import('@/views/category/List.vue'), meta: { title: '分类管理' } },
      { path: 'order/list', name: 'orderList', component: () => import('@/views/order/List.vue'), meta: { title: '订单管理' } },
      { path: 'order/detail/:id', name: 'orderDetail', component: () => import('@/views/order/Detail.vue'), meta: { title: '订单详情' } },
      { path: 'member/list', name: 'memberList', component: () => import('@/views/member/List.vue'), meta: { title: '会员管理' } },
      { path: 'banner/list', name: 'bannerList', component: () => import('@/views/banner/List.vue'), meta: { title: '轮播图管理' } },
      { path: 'notice/list', name: 'noticeList', component: () => import('@/views/notice/List.vue'), meta: { title: '公告管理' } },
      { path: 'comment/list', name: 'commentList', component: () => import('@/views/comment/List.vue'), meta: { title: '评论管理' } },
      { path: 'after-sale/list', name: 'afterSaleList', component: () => import('@/views/afterSale/List.vue'), meta: { title: '售后管理' } },
      { path: 'sys/user', name: 'sysUser', component: () => import('@/views/sys/User.vue'), meta: { title: '管理员管理' } },
      { path: 'service-user/list', name: 'serviceUserList', component: () => import('@/views/serviceUser/List.vue'), meta: { title: '客服管理' } },
      { path: 'seckill/list', name: 'seckillList', component: () => import('@/views/seckill/List.vue'), meta: { title: '秒杀管理' } }
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
