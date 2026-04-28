<template>
  <div class="records">
    <div class="card">
      <div class="card-header">
        <span class="card-title">收支明细</span>
        <div class="header-actions">
          <el-button type="primary" @click="$router.push('/records/add')">
            <el-icon><Plus /></el-icon>
            添加记账
          </el-button>
        </div>
      </div>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部类型" clearable style="width: 120px">
            <el-option label="收入" :value="1" />
            <el-option label="支出" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索描述"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="records" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="categoryName" label="类别" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'success' : 'danger'" size="small">
              {{ row.type === 1 ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :class="row.type === 1 ? 'text-success' : 'text-danger'">
              {{ row.type === 1 ? '+' : '-' }}{{ row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="recordTime" label="记账时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.recordTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadRecords"
        @current-change="loadRecords"
        class="pagination"
      />
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { recordApi } from '@/api/record'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const records = ref([])

const searchForm = reactive({
  type: null,
  dateRange: [],
  keyword: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formatTime = (time: string) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

const loadRecords = async () => {
  loading.value = true
  try {
    const params: any = {
      page: pagination.current,
      size: pagination.size
    }
    if (searchForm.type) params.type = searchForm.type
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }

    const res = await recordApi.getList(params)
    if (res.code === 200) {
      records.value = res.data.records || []
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载记录失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadRecords()
}

const handleReset = () => {
  searchForm.type = null
  searchForm.dateRange = []
  searchForm.keyword = ''
  handleSearch()
}

const handleEdit = (row: any) => {
  router.push(`/records/edit/${row.id}`)
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await recordApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadRecords()
    }
  } catch (error) {
    console.error('删除失败:', error)
  }
}

onMounted(() => {
  loadRecords()
})
</script>

<style lang="scss" scoped>
.records {
  .search-form {
    margin-bottom: 20px;
  }

  .header-actions {
    display: flex;
    align-items: center;
  }

  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
