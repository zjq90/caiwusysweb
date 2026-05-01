<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '240px'" class="sidebar">
      <div class="sidebar-header">
        <h4 v-if="!isCollapse"><el-icon><Coin /></el-icon> 财务管理系统</h4>
        <h4 v-else><el-icon><Coin /></el-icon></h4>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#343a40"
        text-color="rgba(255,255,255,0.8)"
        active-text-color="#fff"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>控制台</template>
        </el-menu-item>
        <el-menu-item index="/admin">
          <el-icon><UserFilled /></el-icon>
          <template #title>管理员管理</template>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><Collection /></el-icon>
          <template #title>收支类别</template>
        </el-menu-item>
        <el-menu-item index="/news">
          <el-icon><Reading /></el-icon>
          <template #title>财务新闻</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><UserFilled /></el-icon>
              {{ userInfo?.realName || userInfo?.username || '管理员' }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
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
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store'
import { 
  Coin, HomeFilled, UserFilled, User, Collection, Reading,
  Fold, Expand, ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const userInfo = computed(() => userStore.userInfo)

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const titleMap = {
    '/dashboard': '控制台',
    '/admin': '管理员管理',
    '/user': '用户管理',
    '/category': '收支类别管理',
    '/news': '财务新闻管理'
  }
  return titleMap[route.path] || '控制台'
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch (error) {
      console.log('取消退出')
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: linear-gradient(180deg, #343a40 0%, #23272b 100%);
  transition: width 0.3s;
}

.sidebar-header {
  padding: 20px;
  background: rgba(0,0,0,0.2);
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.sidebar-header h4 {
  color: #fff;
  margin: 0;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.el-menu {
  border-right: none;
}

.el-menu-item {
  height: 50px;
  line-height: 50px;
}

.el-menu-item.is-active {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%) !important;
}

.header {
  background: #fff;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 25px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
  padding: 5px;
}

.collapse-btn:hover {
  color: #667eea;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.user-info:hover {
  color: #667eea;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
