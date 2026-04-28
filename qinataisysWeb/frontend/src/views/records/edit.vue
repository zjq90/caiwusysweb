<template>
  <div class="record-edit">
    <div class="card">
      <div class="card-header">
        <span class="card-title">{{ isEdit ? '编辑记账' : '添加记账' }}</span>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="form-container"
        label-width="100px"
      >
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type" @change="handleTypeChange">
            <el-radio :value="1">
              <el-icon><TrendCharts /></el-icon>
              收入
            </el-radio>
            <el-radio :value="2">
              <el-icon><ShoppingCart /></el-icon>
              支出
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="类别" prop="categoryId">
          <el-select
            v-model="form.categoryId"
            placeholder="请选择类别"
            style="width: 300px"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="0.01"
            :precision="2"
            :step="100"
            style="width: 300px"
          />
        </el-form-item>

        <el-form-item label="记账时间" prop="recordTime">
          <el-date-picker
            v-model="form.recordTime"
            type="datetime"
            placeholder="选择日期时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 300px"
          />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述（可选）"
            maxlength="500"
            show-word-limit
            style="width: 500px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">
            保存
          </el-button>
          <el-button size="large" @click="handleCancel">
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElForm } from 'element-plus'
import { recordApi } from '@/api/record'
import { categoryApi } from '@/api/category'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const formRef = ref<InstanceType<typeof ElForm>>()
const loading = ref(false)
const categories = ref([])

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  type: 2,
  categoryId: null as number | null,
  amount: 0,
  recordTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
  description: ''
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择类别', trigger: 'change' }],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' },
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
  ],
  recordTime: [{ required: true, message: '请选择记账时间', trigger: 'change' }]
}

const loadCategories = async (type: number) => {
  try {
    const res = await categoryApi.getByType(type)
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('加载类别失败:', error)
  }
}

const handleTypeChange = (type: number) => {
  form.categoryId = null
  loadCategories(type)
}

const loadRecord = async (id: number) => {
  try {
    const res = await recordApi.getDetail(id)
    if (res.code === 200) {
      form.type = res.data.type
      form.categoryId = res.data.categoryId
      form.amount = res.data.amount
      form.recordTime = res.data.recordTime
      form.description = res.data.description || ''
      loadCategories(res.data.type)
    }
  } catch (error) {
    console.error('加载记录失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const data = {
          type: form.type,
          categoryId: form.categoryId,
          amount: form.amount,
          recordTime: form.recordTime,
          description: form.description
        }

        let res
        if (isEdit.value) {
          res = await recordApi.update(Number(route.params.id), data)
        } else {
          res = await recordApi.add(data)
        }

        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
          router.push('/records')
        }
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handleCancel = () => {
  router.back()
}

onMounted(() => {
  if (isEdit.value) {
    loadRecord(Number(route.params.id))
  } else {
    loadCategories(form.type)
  }
})
</script>

<style lang="scss" scoped>
.record-edit {
  .form-container {
    max-width: 600px;
    padding: 20px 0;
  }

  .el-radio {
    display: flex;
    align-items: center;
    gap: 6px;
  }
}
</style>
