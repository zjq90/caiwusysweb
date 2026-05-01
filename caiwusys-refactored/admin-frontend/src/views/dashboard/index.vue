<template>
  <div>
    <div class="welcome-card">
      <h2>欢迎使用财务后台管理系统</h2>
      <p>当前日期: {{ currentDate }}</p>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="icon-box icon-blue">
            <el-icon><UserFilled /></el-icon>
          </div>
          <h3>{{ stats.adminCount }}</h3>
          <p>管理员数量</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="icon-box icon-green">
            <el-icon><User /></el-icon>
          </div>
          <h3>{{ stats.userCount }}</h3>
          <p>用户数量</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="icon-box icon-orange">
            <el-icon><Collection /></el-icon>
          </div>
          <h3>{{ stats.categoryCount }}</h3>
          <p>收支类别</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="icon-box icon-red">
            <el-icon><Reading /></el-icon>
          </div>
          <h3>{{ stats.newsCount }}</h3>
          <p>财务新闻</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminListApi } from '@/api/admin'
import { getUserListApi } from '@/api/user'
import { getCategoryListApi } from '@/api/category'
import { getNewsListApi } from '@/api/news'
import { UserFilled, User, Collection, Reading } from '@element-plus/icons-vue'

const currentDate = ref('')
const stats = ref({
  adminCount: 0,
  userCount: 0,
  categoryCount: 0,
  newsCount: 0
})

const formatDate = () => {
  const now = new Date()
  currentDate.value = `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
}

const loadStats = async () => {
  try {
    const [adminRes, userRes, categoryRes, newsRes] = await Promise.all([
      getAdminListApi({ page: 1, size: 1 }),
      getUserListApi({ page: 1, size: 1 }),
      getCategoryListApi({ page: 1, size: 1 }),
      getNewsListApi({ page: 1, size: 1 })
    ])
    
    stats.value.adminCount = adminRes.data?.total || 0
    stats.value.userCount = userRes.data?.total || 0
    stats.value.categoryCount = categoryRes.data?.total || 0
    stats.value.newsCount = newsRes.data?.total || 0
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  formatDate()
  loadStats()
})
</script>

<style scoped>
.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 15px;
  padding: 30px;
  margin-bottom: 25px;
}

.welcome-card h2 {
  margin-bottom: 10px;
  font-weight: 600;
}

.welcome-card p {
  margin: 0;
  opacity: 0.9;
}

.stat-card {
  background: #fff;
  border-radius: 15px;
  padding: 25px;
  transition: transform 0.3s;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.icon-box {
  width: 60px;
  height: 60px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 15px;
}

.icon-blue {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.icon-green {
  background: rgba(46, 204, 113, 0.1);
  color: #2ecc71;
}

.icon-orange {
  background: rgba(243, 156, 18, 0.1);
  color: #f39c12;
}

.icon-red {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

.stat-card h3 {
  font-weight: 600;
  margin-bottom: 5px;
  font-size: 28px;
  color: #303133;
}

.stat-card p {
  color: #909399;
  margin: 0;
}
</style>
