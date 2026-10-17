<template>
  <el-container class="layout">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="aside">
      <div class="logo">Mall<span>后台管理</span></div>
      <el-menu
        :default-active="$route.path"
        router
        unique-opened
        class="menu"
        background-color="transparent"
        text-color="#94a3b8"
        active-text-color="#ff5000"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon><span>首页统计</span>
        </el-menu-item>

        <el-sub-menu index="goods">
          <template #title>
            <el-icon><Goods /></el-icon><span>商品中心</span>
          </template>
          <el-menu-item index="/product/list">商品管理</el-menu-item>
          <el-menu-item index="/category/list">分类管理</el-menu-item>
          <el-menu-item index="/seckill/list">秒杀管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="trade">
          <template #title>
            <el-icon><ShoppingCart /></el-icon><span>交易中心</span>
          </template>
          <el-menu-item index="/order/list">订单管理</el-menu-item>
          <el-menu-item index="/after-sale/list">售后管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="user">
          <template #title>
            <el-icon><User /></el-icon><span>用户中心</span>
          </template>
          <el-menu-item index="/member/list">会员管理</el-menu-item>
          <el-menu-item index="/comment/list">评论管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="content">
          <template #title>
            <el-icon><Picture /></el-icon><span>内容管理</span>
          </template>
          <el-menu-item index="/banner/list">轮播图管理</el-menu-item>
          <el-menu-item index="/notice/list">公告管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="sys">
          <template #title>
            <el-icon><Setting /></el-icon><span>系统管理</span>
          </template>
          <el-menu-item index="/sys/user">管理员管理</el-menu-item>
          <el-menu-item index="/service-user/list">客服管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶栏 -->
      <el-header class="header">
        <span class="page-title">{{ $route.meta.title }}</span>
        <el-dropdown @command="onCommand">
          <span class="user">
            <el-icon><User /></el-icon>
            {{ userStore.nickname }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const onCommand = async (command) => {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout {
  height: 100%;
}
.aside {
  background: #111827;
  border-right: 1px solid #1f2937;
  display: flex;
  flex-direction: column;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 800;
  color: #ff5000;
}
.logo span {
  color: #e2e8f0;
  font-size: 14px;
  margin-left: 4px;
}
.menu {
  border-right: none;
  flex: 1;
  padding: 8px 0;
}
.menu :deep(.el-sub-menu__title) {
  height: 48px;
  line-height: 48px;
}
.menu :deep(.el-sub-menu .el-menu-item) {
  height: 40px;
  line-height: 40px;
  min-width: 0;
  color: #94a3b8;
}
.menu :deep(.el-sub-menu .el-menu-item:hover) {
  color: #e2e8f0;
  background: rgba(255, 255, 255, 0.04);
}
.header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #111827;
  border-bottom: 1px solid #1f2937;
  padding: 0 20px;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #e2e8f0;
}
.user {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: #cbd5e1;
  font-size: 14px;
}
.main {
  padding: 0;
  overflow-y: auto;
}
</style>
