<template>
  <div class="profile">
    <el-row :gutter="20">
      <el-col :span="8">
        <div class="card">
          <div class="user-info">
            <el-avatar :size="100" icon="UserFilled" />
            <div class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</div>
            <div class="user-username">@{{ userStore.userInfo?.username }}</div>
          </div>
          <el-divider />
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">{{ stats.records }}</div>
              <div class="stat-label">记账数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.wishes }}</div>
              <div class="stat-label">心愿数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.memos }}</div>
              <div class="stat-label">备忘数</div>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="16">
        <div class="card">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="info">
              <el-form
                ref="infoFormRef"
                :model="infoForm"
                :rules="infoRules"
                label-width="100px"
                class="form-container"
              >
                <el-form-item label="用户名">
                  <el-input v-model="userStore.userInfo?.username" disabled />
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="infoForm.nickname" placeholder="请输入昵称" maxlength="50" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="infoForm.email" placeholder="请输入邮箱" maxlength="100" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="infoForm.phone" placeholder="请输入手机号" maxlength="20" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="infoLoading" @click="handleSaveInfo">
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="修改密码" name="password">
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="100px"
                class="form-container"
              >
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入旧密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码（6-20个字符）"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="passwordLoading" @click="handleSavePassword">
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElForm } from 'element-plus'

const userStore = useUserStore()
const activeTab = ref('info')
const infoFormRef = ref<InstanceType<typeof ElForm>>()
const passwordFormRef = ref<InstanceType<typeof ElForm>>()
const infoLoading = ref(false)
const passwordLoading = ref(false)

const stats = reactive({
  records: 0,
  wishes: 0,
  memos: 0
})

const infoForm = reactive({
  nickname: '',
  email: '',
  phone: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
  } else {
    callback()
  }
}

const infoRules = {
  nickname: [{ min: 0, max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }],
  email: [
    { pattern: /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/, message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const loadUserInfo = () => {
  if (userStore.userInfo) {
    infoForm.nickname = userStore.userInfo.nickname || ''
    infoForm.email = userStore.userInfo.email || ''
    infoForm.phone = userStore.userInfo.phone || ''
  }
}

const handleSaveInfo = async () => {
  if (!infoFormRef.value) return
  
  await infoFormRef.value.validate(async (valid) => {
    if (valid) {
      infoLoading.value = true
      try {
        const res = await userStore.updateUserInfo({
          nickname: infoForm.nickname,
          email: infoForm.email,
          phone: infoForm.phone
        })
        if (res.code === 200) {
          ElMessage.success('保存成功')
        }
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        infoLoading.value = false
      }
    }
  })
}

const handleSavePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        const res = await userStore.updatePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
          confirmPassword: passwordForm.confirmPassword
        })
        if (res.code === 200) {
          ElMessage.success('密码修改成功')
          passwordForm.oldPassword = ''
          passwordForm.newPassword = ''
          passwordForm.confirmPassword = ''
        }
      } catch (error) {
        console.error('修改密码失败:', error)
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.profile {
  .user-info {
    text-align: center;
    padding: 20px 0;

    .user-name {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin-top: 15px;
    }

    .user-username {
      font-size: 14px;
      color: #909399;
      margin-top: 5px;
    }
  }

  .user-stats {
    display: flex;
    justify-content: space-around;

    .stat-item {
      text-align: center;

      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #409eff;
      }

      .stat-label {
        font-size: 13px;
        color: #909399;
        margin-top: 5px;
      }
    }
  }

  .form-container {
    max-width: 500px;
    padding: 20px 0;
  }
}
</style>
