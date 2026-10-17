<template>
  <div class="layout">
    <!-- 顶部栏 -->
    <header class="header">
      <div class="container header-inner">
        <router-link to="/" class="logo">Mall<span>商城</span></router-link>

        <div class="search">
          <el-input
            v-model="keyword"
            placeholder="搜索商品"
            clearable
            @keyup.enter="onSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="onSearch" />
            </template>
          </el-input>
        </div>

        <div class="header-actions">
          <el-badge :value="cartStore.count" :hidden="cartStore.count === 0" class="cart-badge">
            <router-link to="/cart" class="icon-btn" title="购物车">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </router-link>
          </el-badge>

          <template v-if="userStore.isLogin">
            <el-dropdown @command="onUserCommand">
              <span class="user-name">
                <el-icon><User /></el-icon>
                {{ userStore.nickname }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="order">我的订单</el-dropdown-item>
                  <el-dropdown-item command="afterSale">我的售后</el-dropdown-item>
                  <el-dropdown-item command="chat">联系客服</el-dropdown-item>
                  <el-dropdown-item command="address">收货地址</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="link">登录</router-link>
            <router-link to="/register" class="link">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 分类导航 -->
    <nav class="nav">
      <div class="container nav-inner">
        <router-link to="/" class="nav-item" :class="{ active: $route.path === '/' }">首页</router-link>
        <router-link to="/seckill" class="nav-item" :class="{ active: $route.path === '/seckill' }">秒杀</router-link>
        <el-dropdown
          v-for="cat in categories"
          :key="cat.id"
          @command="(id) => goCategory(id)"
        >
          <span class="nav-item" :class="{ active: isCatActive(cat) }" @click="goCategory(cat.id)">
            {{ cat.name }}
            <el-icon class="nav-arrow"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :command="cat.id">全部{{ cat.name }}</el-dropdown-item>
              <el-dropdown-item v-for="child in cat.children" :key="child.id" :command="child.id">
                {{ child.name }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </nav>

    <!-- 内容区 -->
    <main class="main">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <p>Mall 商城 电商系统</p>
        <p class="sub">感谢您的使用</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { Search, ShoppingCart, User, ArrowDown } from '@element-plus/icons-vue'
import { getCategoryList } from '@/api'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const keyword = ref('')
const categories = ref([])

// 判断当前路由的分类是否命中该大类或其子类（用于高亮）
const isCatActive = (cat) => {
  const id = route.query.categoryId
  if (!id) return false
  if (String(cat.id) === String(id)) return true
  return (cat.children || []).some((c) => String(c.id) === String(id))
}

const onSearch = () => {
  const kw = keyword.value.trim()
  router.push({ path: '/product/list', query: kw ? { keyword: kw } : {} })
}

const goCategory = (id) => {
  router.push({ path: '/product/list', query: { categoryId: id } })
}

const onUserCommand = async (command) => {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    userStore.logout()
    cartStore.refreshCount()
    router.push('/')
  } else if (command === 'profile') {
    router.push('/user/profile')
  } else if (command === 'order') {
    router.push('/order/list')
  } else if (command === 'afterSale') {
    router.push('/after-sale')
  } else if (command === 'chat') {
    router.push('/chat')
  } else if (command === 'address') {
    router.push('/user/address')
  }
}

onMounted(async () => {
  cartStore.refreshCount()
  try {
    categories.value = await getCategoryList()
  } catch (e) {
    /* ignore */
  }
})
</script>

<style scoped>
.layout {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

/* 顶部栏 */
.header {
  background: #fff;
  border-bottom: 1px solid var(--mall-border);
}
.header-inner {
  height: 72px;
  display: flex;
  align-items: center;
  gap: 40px;
}
.logo {
  font-size: 24px;
  font-weight: 800;
  color: var(--mall-primary);
  white-space: nowrap;
}
.logo span {
  color: #333;
  margin-left: 2px;
}
.search {
  flex: 1;
  max-width: 480px;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-left: auto;
}
.icon-btn {
  display: inline-flex;
  align-items: center;
  color: #555;
}
.cart-badge {
  cursor: pointer;
}
.user-name {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: #333;
  font-size: 14px;
}
.link {
  color: #555;
  font-size: 14px;
}
.link:hover {
  color: var(--mall-primary);
}

/* 分类导航 */
.nav {
  background: #fff;
  border-bottom: 1px solid var(--mall-border);
}
.nav-inner {
  display: flex;
  align-items: center;
  height: 44px;
  gap: 28px;
}
.nav-item {
  cursor: pointer;
  font-size: 14px;
  color: #333;
  height: 44px;
  display: inline-flex;
  align-items: center;
  gap: 2px;
}
.nav-arrow {
  font-size: 12px;
}
.nav-item:hover,
.nav-item.active {
  color: var(--mall-primary);
}
.nav-item.active {
  border-bottom: 2px solid var(--mall-primary);
}

/* 内容区 */
.main {
  flex: 1;
  padding-bottom: 40px;
}

/* 页脚 */
.footer {
  background: #fff;
  border-top: 1px solid var(--mall-border);
  padding: 24px 0;
  text-align: center;
  color: #666;
  font-size: 13px;
}
.footer .sub {
  margin-top: 6px;
  color: #aaa;
  font-size: 12px;
}
</style>
