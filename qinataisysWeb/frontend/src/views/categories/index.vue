<template>
  <div class="categories">
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">收入类别</span>
            <el-button type="primary" size="small" @click="openAddDialog(1)">
              <el-icon><Plus /></el-icon>
              添加
            </el-button>
          </div>
          <el-table :data="incomeCategories" style="width: 100%" v-loading="loading">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="name" label="类别名称" />
            <el-table-column prop="icon" label="图标" width="100">
              <template #default="{ row }">
                <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
                <span v-else class="text-info">-</span>
              </template>
            </el-table-column>
            <el-table-column prop="sort" label="排序" width="80" />
            <el-table-column prop="userId" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.userId === 0 ? 'info' : 'primary'" size="small">
                  {{ row.userId === 0 ? '系统默认' : '自定义' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
                <el-button
                  type="danger"
                  link
                  size="small"
                  @click="handleDelete(row)"
                  :disabled="row.userId === 0"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>

      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">支出类别</span>
            <el-button type="primary" size="small" @click="openAddDialog(2)">
              <el-icon><Plus /></el-icon>
              添加
            </el-button>
          </div>
          <el-table :data="expenseCategories" style="width: 100%" v-loading="loading">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="name" label="类别名称" />
            <el-table-column prop="icon" label="图标" width="100">
              <template #default="{ row }">
                <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
                <span v-else class="text-info">-</span>
              </template>
            </el-table-column>
            <el-table-column prop="sort" label="排序" width="80" />
            <el-table-column prop="userId" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.userId === 0 ? 'info' : 'primary'" size="small">
                  {{ row.userId === 0 ? '系统默认' : '自定义' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
                <el-button
                  type="danger"
                  link
                  size="small"
                  @click="handleDelete(row)"
                  :disabled="row.userId === 0"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>

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
        <el-form-item label="类别名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入类别名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入图标名称（可选）" maxlength="50" />
          <div class="tips">填写Element Plus图标名称，如：Wallet、ShoppingCart等</div>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="100" />
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
import { reactive, ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElForm } from 'element-plus'
import { categoryApi } from '@/api/category'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref<InstanceType<typeof ElForm>>()
const incomeCategories = ref([])
const expenseCategories = ref([])
const isEdit = ref(false)

const form = reactive({
  id: null as number | null,
  type: 1,
  name: '',
  icon: '',
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入类别名称', trigger: 'blur' }]
}

const dialogTitle = computed(() => (isEdit.value ? '编辑类别' : '添加类别'))

const loadCategories = async () => {
  loading.value = true
  try {
    const [incomeRes, expenseRes] = await Promise.all([
      categoryApi.getIncomeList(),
      categoryApi.getExpenseList()
    ])
    if (incomeRes.code === 200) incomeCategories.value = incomeRes.data
    if (expenseRes.code === 200) expenseCategories.value = expenseRes.data
  } catch (error) {
    console.error('加载类别失败:', error)
  } finally {
    loading.value = false
  }
}

const openAddDialog = (type: number) => {
  isEdit.value = false
  form.id = null
  form.type = type
  form.name = ''
  form.icon = ''
  form.sort = 0
  dialogVisible.value = true
}

const openEditDialog = (row: any) => {
  isEdit.value = true
  form.id = row.id
  form.type = row.type
  form.name = row.name
  form.icon = row.icon || ''
  form.sort = row.sort || 0
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const data = {
          type: form.type,
          name: form.name,
          icon: form.icon,
          sort: form.sort
        }

        let res
        if (isEdit.value) {
          res = await categoryApi.update(form.id!, data)
        } else {
          res = await categoryApi.add(data)
        }

        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
          dialogVisible.value = false
          loadCategories()
        }
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确定要删除这个类别吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await categoryApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadCategories()
    }
  } catch (error) {
    console.error('删除失败:', error)
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style lang="scss" scoped>
.categories {
  .tips {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }
}
</style>
