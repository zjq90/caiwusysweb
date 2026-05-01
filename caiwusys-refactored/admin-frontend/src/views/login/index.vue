<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-title">
        <h3><el-icon size="28"><Coin /></el-icon> 财务后台管理系统</h3>
        <p>请登录您的账户</p>
      </div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" class="login-btn" size="large">
            <el-icon v-if="!loading"><Right /></el-icon>
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="text-center mt-3">
        <small class="text-muted">默认账号: admin / 123456</small>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store'
import { Coin, User, Lock, Right } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: '123456'
})

const validateUsername = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else {
    callback()
  }
}

const loginRules = {
  username: [{ required: true, validator: validateUsername, trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(loginForm.username, loginForm.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  padding: 40px;
  width: 100%;
  max-width: 420px;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #495057;
}

.login-title h3 {
  font-weight: 600;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.login-title p {
  color: #6c757d;
  font-size: 14px;
}

.login-form {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 45px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.text-center {
  text-align: center;
}

.mt-3 {
  margin-top: 1rem;
}

.text-muted {
  color: #6c757d;
}
</style>
