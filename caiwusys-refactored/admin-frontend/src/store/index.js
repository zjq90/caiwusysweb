import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, removeToken, setUserInfo, getUserInfo, removeUserInfo } from '@/utils/auth'
import { loginApi, getAdminInfoApi } from '@/api/admin'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref(getUserInfo())

  const isLoggedIn = computed(() => !!token.value)

  async function login(username, password) {
    const res = await loginApi(username, password)
    token.value = res.data.token
    userInfo.value = res.data.admin
    setToken(res.data.token)
    setUserInfo(res.data.admin)
    return res
  }

  async function getInfo() {
    const res = await getAdminInfoApi()
    userInfo.value = res.data
    setUserInfo(res.data)
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    removeToken()
    removeUserInfo()
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    getInfo,
    logout
  }
})
