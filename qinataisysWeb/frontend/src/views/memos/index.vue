<template>
  <div class="memos">
    <div class="card">
      <div class="card-header">
        <span class="card-title">我的备忘录</span>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>
          添加备忘
        </el-button>
      </div>

      <el-table :data="memos" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="reminderTime" label="提醒时间" width="160">
          <template #default="{ row }">
            {{ row.reminderTime ? formatTime(row.reminderTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="important" label="重要" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.important" type="danger" size="small">重要</el-tag>
            <span v-else class="text-info">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">
              {{ row.status === 1 ? '已完成' : '待办' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button
              type="success"
              link
              size="small"
              @click="handleComplete(row)"
              v-if="row.status === 0"
            >
              完成
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入备忘标题" maxlength="100" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入备忘内容"
          />
        </el-form-item>
        <el-form-item label="提醒时间" prop="reminderTime">
          <el-date-picker
            v-model="form.reminderTime"
            type="datetime"
            placeholder="选择提醒时间（可选）"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="重要" prop="important">
          <el-switch v-model="form.important" active-text="是" inactive-text="否" />
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="isEdit">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">待办</el-radio>
            <el-radio :value="1">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElForm } from 'element-plus'
import { memoApi } from '@/api/memo'
import dayjs from 'dayjs'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref<InstanceType<typeof ElForm>>()
const memos = ref([])
const isEdit = ref(false)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null as number | null,
  title: '',
  content: '',
  reminderTime: '',
  important: 0,
  status: 0
})

const rules = {
  title: [{ required: true, message: '请输入备忘标题', trigger: 'blur' }]
}

const dialogTitle = computed(() => (isEdit.value ? '编辑备忘' : '添加备忘'))

const formatTime = (time: string) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await memoApi.getList({
      page: pagination.current,
      size: pagination.size
    })
    if (res.code === 200) {
      memos.value = res.data.records || []
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载备忘录失败:', error)
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.id = null
  form.title = ''
  form.content = ''
  form.reminderTime = ''
  form.important = 0
  form.status = 0
  dialogVisible.value = true
}

const openEditDialog = (memo: any) => {
  isEdit.value = true
  form.id = memo.id
  form.title = memo.title
  form.content = memo.content || ''
  form.reminderTime = memo.reminderTime || ''
  form.important = memo.important || 0
  form.status = memo.status || 0
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const data = {
          title: form.title,
          content: form.content,
          reminderTime: form.reminderTime || null,
          important: form.important,
          status: form.status
        }

        let res
        if (isEdit.value) {
          res = await memoApi.update(form.id!, data)
        } else {
          res = await memoApi.add(data)
        }

        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleComplete = async (memo: any) => {
  try {
    const res = await memoApi.update(memo.id, { status: 1 })
    if (res.code === 200) {
      ElMessage.success('已完成')
      loadData()
    }
  } catch (error) {
    console.error('更新失败:', error)
  }
}

const handleDelete = async (memo: any) => {
  await ElMessageBox.confirm('确定要删除这个备忘吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await memoApi.delete(memo.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error) {
    console.error('删除失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.memos {
  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
