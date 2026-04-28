<template>
  <div class="news-detail">
    <div class="card">
      <div class="header-actions">
        <el-button link @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>
      <div class="news-title">{{ news.title }}</div>
      <div class="news-meta">
        <span class="news-source">来源: {{ news.source || '未知' }}</span>
        <span class="news-time">发布时间: {{ formatTime(news.publishTime) }}</span>
        <span class="news-views">
          <el-icon><View /></el-icon>
          {{ news.viewCount || 0 }} 阅读
        </span>
      </div>
      <div class="news-summary" v-if="news.summary">
        <strong>摘要：</strong>{{ news.summary }}
      </div>
      <div class="news-content" v-html="news.content"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { newsApi } from '@/api/news'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const news = ref<any>({})

const formatTime = (time: string) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

const loadData = async () => {
  try {
    const res = await newsApi.getDetail(Number(route.params.id))
    if (res.code === 200) {
      news.value = res.data
    }
  } catch (error) {
    console.error('加载新闻详情失败:', error)
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.news-detail {
  .header-actions {
    margin-bottom: 20px;
  }

  .news-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    line-height: 1.4;
    margin-bottom: 15px;
  }

  .news-meta {
    display: flex;
    align-items: center;
    gap: 20px;
    font-size: 14px;
    color: #909399;
    padding-bottom: 20px;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 20px;

    .news-views {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  .news-summary {
    background: #f5f7fa;
    padding: 15px;
    border-radius: 4px;
    margin-bottom: 20px;
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
  }

  .news-content {
    font-size: 16px;
    line-height: 2;
    color: #303133;

    :deep(p) {
      margin-bottom: 15px;
      text-indent: 2em;
    }

    :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
      margin: 20px 0 15px;
      font-weight: 600;
    }

    :deep(ul), :deep(ol) {
      margin: 10px 0;
      padding-left: 2em;

      li {
        margin: 5px 0;
      }
    }

    :deep(strong) {
      font-weight: 600;
    }
  }
}
</style>
