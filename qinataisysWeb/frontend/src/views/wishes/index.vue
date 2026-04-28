<template>
  <div class="wishes">
    <div class="card">
      <div class="card-header">
        <span class="card-title">我的心愿</span>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>
          添加心愿
        </el-button>
      </div>

      <el-row :gutter="20" v-if="wishes.length">
        <el-col :span="8" v-for="wish in wishes" :key="wish.id">
          <div class="wish-card" :class="{ completed: wish.status === 1 }">
            <div class="wish-header">
              <span class="wish-title">{{ wish.title }}</span>
              <el-tag :type="wish.status === 1 ? 'success' : 'warning'" size="small">
                {{ wish.status === 1 ? '已完成' : '进行中' }}
              </el-tag>
            </div>
            <div class="wish-desc" v-if="wish.description">
              {{ wish.description }}
            </div>
            <div class="wish-amount">
              <div class="amount-item">
                <span class="label">已存</span>
                <span class="value current">¥{{ wish.currentAmount || 0 }}</span>
              </div>
              <div class="amount-item">
                <span class="label">目标</span>
                <span class="value">¥{{ wish.targetAmount }}</span>
              </div>
              <div class="amount-item" v-if="wish.deadline">
                <span class="label">截止</span>
                <span class="value">{{ formatDate(wish.deadline) }}</span>
              </div>
            </div>
            <el-progress
              :percentage="wish.progress || 0"
              :status="wish.progress >= 100 ? 'success' : ''"
              :stroke-width="10"
            />
            <div class="wish-actions">
              <el-button type="primary" link size="small" @click="openEditDialog(wish)">编辑</el-button>
              <el-button
                type="success"
                link
                size="small"
                @click="handleComplete(wish)"
                v-if="wish.status === 0"
              >
                完成
              </el-button>
              <el-button type="danger" link size="small" @click="handleDelete(wish)">删除</el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-empty v-else description="暂无心愿，快来添加一个吧！" />
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
          <el-input v-model="form.title" placeholder="请输入心愿标题" maxlength="100" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="2"
            placeholder="请输入描述（可选）"
            maxlength="500"
          />
        </el-form-item>
        <el-form-item label="目标金额" prop="targetAmount">
          <el-input-number
            v-model="form.targetAmount"
            :min="0.01"
            :precision="2"
            :step="100"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="当前金额" prop="currentAmount">
          <el-input-number
            v-model="form.currentAmount"
            :min="0"
            :precision="2"
            :step="100"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="截止日期" prop="deadline">
          <el-date-picker
            v-model="form.deadline"
            type="date"
            placeholder="选择截止日期（可选）"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="isEdit">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">进行中</el-radio>
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
import { wishApi } from '@/api/wish'
import dayjs from 'dayjs'

const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref<InstanceType<typeof ElForm>>()
const wishes = ref([])
const isEdit = ref(false)

const form = reactive({
  id: null as number | null,
  title: '',
  description: '',
  targetAmount: 0,
  currentAmount: 0,
  deadline: '',
  status: 0
})

const rules = {
  title: [{ required: true, message: '请输入心愿标题', trigger: 'blur' }],
  targetAmount: [
    { required: true, message: '请输入目标金额', trigger: 'blur' },
    {
      validator: (rule: any, value: number, callback: any) => {
        if (value <= 0) {
          callback(new Error('金额必须大于0'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const dialogTitle = computed(() => (isEdit.value ? '编辑心愿' : '添加心愿'))

const formatDate = (date: string) => {
  return date ? dayjs(date).format('YYYY-MM-DD') : '-'
}

const loadData = async () => {
  try {
    const res = await wishApi.getMyWishes()
    if (res.code === 200) {
      wishes.value = res.data
    }
  } catch (error) {
    console.error('加载心愿单失败:', error)
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.id = null
  form.title = ''
  form.description = ''
  form.targetAmount = 0
  form.currentAmount = 0
  form.deadline = ''
  form.status = 0
  dialogVisible.value = true
}

const openEditDialog = (wish: any) => {
  isEdit.value = true
  form.id = wish.id
  form.title = wish.title
  form.description = wish.description || ''
  form.targetAmount = wish.targetAmount
  form.currentAmount = wish.currentAmount || 0
  form.deadline = wish.deadline || ''
  form.status = wish.status
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
          description: form.description,
          targetAmount: form.targetAmount,
          currentAmount: form.currentAmount,
          deadline: form.deadline || null,
          status: form.status
        }

        let res
        if (isEdit.value) {
          res = await wishApi.update(form.id!, data)
        } else {
          res = await wishApi.add(data)
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

const handleComplete = async (wish: any) => {
  await ElMessageBox.confirm('确定要标记这个心愿为已完成吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  })
  
  try {
    const res = await wishApi.update(wish.id, { status: 1 })
    if (res.code === 200) {
      ElMessage.success('已完成')
      loadData()
    }
  } catch (error) {
    console.error('更新失败:', error)
  }
}

const handleDelete = async (wish: any) => {
  await ElMessageBox.confirm('确定要删除这个心愿吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await wishApi.delete(wish.id)
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
.wishes {
  .wish-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.15);
    }

    &.completed {
      opacity: 0.7;
    }

    .wish-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;

      .wish-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }

    .wish-desc {
      font-size: 13px;
      color: #909399;
      margin-bottom: 15px;
      line-height: 1.5;
    }

    .wish-amount {
      display: flex;
      justify-content: space-between;
      margin-bottom: 15px;

      .amount-item {
        text-align: center;

        .label {
          font-size: 12px;
          color: #909399;
          display: block;
        }

        .value {
          font-size: 16px;
          font-weight: 600;
          color: #303133;

          &.current {
            color: #67c23a;
          }
        }
      }
    }

    .wish-actions {
      display: flex;
      justify-content: flex-end;
      margin-top: 10px;
      gap: 10px;
    }
  }
}
</style>
