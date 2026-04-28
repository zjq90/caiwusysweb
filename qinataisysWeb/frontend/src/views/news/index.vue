<template>
  <div class="news">
    <div class="card">
      <div class="card-header">
        <span class="card-title">财务新闻</span>
        <el-input
          v-model="keyword"
          placeholder="搜索新闻"
          clearable
          style="width: 250px"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="news-list" v-if="newsList.length">
        <div
          class="news-item"
          v-for="news in newsList"
          :key="news.id"
          @click="goToDetail(news.id)"
        >
          <div class="news-title">{{ news.title }}</div>
          <div class="news-summary">{{ news.summary }}</div>
          <div class="news-meta">
            <span class="news-source">{{ news.source || '未知来源' }}</span>
            <span class="news-time">{{ formatTime(news.publishTime) }}</span>
            <span class="news-views">
              <el-icon><View /></el-icon>
              {{ news.viewCount || 0 }}
            </span>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无新闻" />

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { newsApi } from '@/api/news'
import dayjs from 'dayjs'

const router = useRouter()
const keyword = ref('')
const newsList = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formatTime = (time: string) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

const loadData = async () => {
  try {
    const res = await newsApi.getList({
      page: pagination.current,
      size: pagination.size,
      keyword: keyword.value || undefined
    })
    if (res.code === 200) {
      newsList.value = res.data.records || []
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载新闻失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const goToDetail = (id: number) => {
  router.push(`/news/${id}`)
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.news {
  .news-list {
    margin-top: 10px;
  }

  .news-item {
    padding: 20px 0;
    border-bottom: 1px solid #ebeef5;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      .news-title {
        color: #409eff;
      }
    }

    &:last-child {
      border-bottom: none;
    }

    .news-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 10px;
      line-height: 1.4;
    }

    .news-summary {
      font-size: 14px;
      color: #606266;
      line-height: 1.6;
      margin-bottom: 12px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .news-meta {
      display: flex;
      align-items: center;
      gap: 20px;
      font-size: 13px;
      color: #909399;

      .news-views {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }

  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
