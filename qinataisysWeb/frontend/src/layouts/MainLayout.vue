<template>
  <el-container class="main-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon :size="28" color="#409eff"><Wallet /></el-icon>
        <span class="logo-text">财务系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-sub-menu index="records-menu">
          <template #title>
            <el-icon><Notebook /></el-icon>
            <span>财务管理</span>
          </template>
          <el-menu-item index="/records">收支明细</el-menu-item>
          <el-menu-item index="/records/add">添加记账</el-menu-item>
          <el-menu-item index="/categories">收支类别</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="statistics-menu">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>统计分析</span>
          </template>
          <el-menu-item index="/statistics">财务统计</el-menu-item>
          <el-menu-item index="/analysis">财务分析</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/budgets">
          <el-icon><Coin /></el-icon>
          <span>财务预算</span>
        </el-menu-item>

        <el-menu-item index="/wishes">
          <el-icon><Star /></el-icon>
          <span>心愿单</span>
        </el-menu-item>

        <el-menu-item index="/memos">
          <el-icon><Document /></el-icon>
          <span>备忘录</span>
        </el-menu-item>

        <el-menu-item index="/news">
          <el-icon><News /></el-icon>
          <span>财务新闻</span>
        </el-menu-item>

        <el-menu-item index="/calculator">
          <el-icon><Calculator /></el-icon>
          <span>计算器</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.name !== 'Dashboard'">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route)

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style lang="scss" scoped>
.main-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: rgba(255, 255, 255, 0.2);
    border-radius: 3px;
  }
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  
  .logo-text {
    margin-left: 10px;
    font-size: 18px;
    font-weight: 600;
    color: #fff;
  }
}

.menu {
  border-right: none;
  
  .el-menu-item,
  .el-sub-menu__title {
    height: 50px;
    line-height: 50px;
    
    &:hover {
      background-color: rgba(255, 255, 255, 0.1);
    }
  }
  
  .el-menu-item.is-active {
    background-color: #409eff !important;
    color: #fff !important;
  }
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  
  .user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    
    .username {
      margin: 0 8px;
      font-size: 14px;
    }
  }
}

.main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: #c0c4cc;
    border-radius: 3px;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
