<template>
  <div>
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="新闻标题">
          <el-input v-model="searchForm.title" placeholder="请输入新闻标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="已发布" :value="1" />
            <el-option label="草稿" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加新闻
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="新闻标题" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.title" placement="top">
              <span>{{ row.title }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="120">
          <template #default="{ row }">
            {{ row.author || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览次数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span :class="row.status === 1 ? 'status-active' : 'status-inactive'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑新闻' : '添加新闻'" width="800px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="新闻标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入新闻标题" />
        </el-form-item>
        <el-form-item label="新闻摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入新闻摘要（可选）" />
        </el-form-item>
        <el-form-item label="新闻内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入新闻内容" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="封面图片">
              <el-input v-model="form.coverImage" placeholder="请输入封面图片URL（可选）" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者">
              <el-input v-model="form.author" placeholder="请输入作者（可选）" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="已发布" :value="1" />
                <el-option label="草稿" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getNewsListApi, getNewsByIdApi, addNewsApi, updateNewsApi, deleteNewsApi 
} from '@/api/news'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const tableData = ref([])

const searchForm = reactive({
  title: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  author: '',
  viewCount: 0,
  status: 1
})

const rules = {
  title: [{ required: true, message: '新闻标题不能为空', trigger: 'blur' }]
}

const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const res = await getNewsListApi(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.status = null
  pagination.current = 1
  loadData()
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadData()
}

const resetForm = () => {
  form.id = null
  form.title = ''
  form.summary = ''
  form.content = ''
  form.coverImage = ''
  form.author = ''
  form.viewCount = 0
  form.status = 1
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isEdit.value = true
  resetForm()
  try {
    const res = await getNewsByIdApi(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取新闻信息失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateNewsApi(form)
      ElMessage.success('修改成功')
    } else {
      await addNewsApi(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条数据吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteNewsApi(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
</style>
