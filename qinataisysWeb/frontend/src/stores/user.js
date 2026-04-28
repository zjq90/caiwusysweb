import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  async function login(loginForm) {
    const res = await userApi.login(loginForm)
    if (res.code === 200) {
      token.value = res.data.token
      userInfo.value = res.data.user
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data.user))
    }
    return res
  }

  async function register(registerForm) {
    return await userApi.register(registerForm)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  async function getUserInfo() {
    const res = await userApi.getUserInfo()
    if (res.code === 200) {
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    }
    return res
  }

  async function updateUserInfo(data) {
    const res = await userApi.updateUserInfo(data)
    if (res.code === 200 && userInfo.value) {
      userInfo.value = { ...userInfo.value, ...data }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
    return res
  }

  async function updatePassword(data) {
    return await userApi.updatePassword(data)
  }

  return {
    token,
    userInfo,
    login,
    register,
    logout,
    getUserInfo,
    updateUserInfo,
    updatePassword
  }
})
