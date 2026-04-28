<template>
  <div class="budgets">
    <div class="card">
      <div class="card-header">
        <span class="card-title">财务预算</span>
        <div class="header-actions">
          <el-date-picker
            v-model="selectedMonth"
            type="month"
            value-format="YYYY-MM"
            placeholder="选择月份"
            @change="loadData"
          />
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon>
            添加预算
          </el-button>
        </div>
      </div>

      <el-row :gutter="20" v-if="budgets.length">
        <el-col :span="8" v-for="budget in budgets" :key="budget.id">
          <div class="budget-card">
            <div class="budget-header">
              <span class="budget-name">{{ budget.categoryName || '总预算' }}</span>
              <div class="budget-actions">
                <el-button type="primary" link size="small" @click="openEditDialog(budget)">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDelete(budget)">删除</el-button>
              </div>
            </div>
            <div class="budget-amount">
              <span class="used">¥{{ budget.usedAmount || 0 }}</span>
              <span class="separator">/</span>
              <span class="total">¥{{ budget.amount }}</span>
            </div>
            <el-progress
              :percentage="budget.usageRate || 0"
              :status="budget.usageRate >= 100 ? 'exception' : budget.usageRate >= 80 ? 'warning' : ''"
            />
            <div class="budget-info">
              <span class="remaining">剩余: ¥{{ budget.remainingAmount || budget.amount }}</span>
              <span class="rate">使用率: {{ budget.usageRate || 0 }}%</span>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-empty v-else description="暂无预算数据" />
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
        <el-form-item label="年份" prop="year">
          <el-date-picker
            v-model="form.year"
            type="year"
            value-format="YYYY"
            placeholder="选择年份"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="月份" prop="month">
          <el-select v-model="form.month" placeholder="选择月份" style="width: 100%">
            <el-option v-for="m in 12" :key="m" :label="m + '月'" :value="m" />
          </el-select>
        </el-form-item>
        <el-form-item label="预算类别" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="不选则为总预算" clearable style="width: 100%">
            <el-option
              v-for="category in expenseCategories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预算金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="0.01"
            :precision="2"
            :step="100"
            style="width: 100%"
          />
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
import { budgetApi } from '@/api/budget'
import { categoryApi } from '@/api/category'
import dayjs from 'dayjs'

const selectedMonth = ref(dayjs().format('YYYY-MM'))
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref<InstanceType<typeof ElForm>>()
const budgets = ref([])
const expenseCategories = ref([])
const isEdit = ref(false)

const form = reactive({
  id: null as number | null,
  year: dayjs().format('YYYY'),
  month: dayjs().month() + 1,
  categoryId: null as number | null,
  amount: 0,
  description: ''
})

const rules = {
  year: [{ required: true, message: '请选择年份', trigger: 'change' }],
  month: [{ required: true, message: '请选择月份', trigger: 'change' }],
  amount: [
    { required: true, message: '请输入预算金额', trigger: 'blur' },
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

const dialogTitle = computed(() => (isEdit.value ? '编辑预算' : '添加预算'))

const loadData = async () => {
  try {
    const year = Number(selectedMonth.value.split('-')[0])
    const month = Number(selectedMonth.value.split('-')[1])
    const res = await budgetApi.getByMonth(year, month)
    if (res.code === 200) {
      budgets.value = res.data
    }
  } catch (error) {
    console.error('加载预算失败:', error)
  }
}

const loadCategories = async () => {
  try {
    const res = await categoryApi.getExpenseList()
    if (res.code === 200) {
      expenseCategories.value = res.data
    }
  } catch (error) {
    console.error('加载类别失败:', error)
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.id = null
  form.year = dayjs().format('YYYY')
  form.month = dayjs().month() + 1
  form.categoryId = null
  form.amount = 0
  form.description = ''
  dialogVisible.value = true
}

const openEditDialog = (budget: any) => {
  isEdit.value = true
  form.id = budget.id
  form.year = String(budget.year)
  form.month = budget.month
  form.categoryId = budget.categoryId
  form.amount = budget.amount
  form.description = budget.description || ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const data = {
          year: Number(form.year),
          month: form.month,
          categoryId: form.categoryId,
          amount: form.amount,
          description: form.description
        }

        let res
        if (isEdit.value) {
          res = await budgetApi.update(form.id!, data)
        } else {
          res = await budgetApi.add(data)
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

const handleDelete = async (budget: any) => {
  await ElMessageBox.confirm('确定要删除这个预算吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await budgetApi.delete(budget.id)
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
  loadCategories()
})
</script>

<style lang="scss" scoped>
.budgets {
  .header-actions {
    display: flex;
    align-items: center;
    gap: 15px;
  }

  .budget-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.15);
    }

    .budget-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      .budget-name {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }

    .budget-amount {
      margin-bottom: 15px;
      font-size: 20px;

      .used {
        color: #f56c6c;
        font-weight: 600;
      }

      .separator {
        color: #909399;
        margin: 0 5px;
      }

      .total {
        color: #606266;
      }
    }

    .budget-info {
      display: flex;
      justify-content: space-between;
      margin-top: 10px;
      font-size: 13px;
      color: #909399;
    }
  }
}
</style>
