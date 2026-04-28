import request from '@/utils/request'

export const userApi = {
  login(data) {
    return request.post('/auth/login', data)
  },

  register(data) {
    return request.post('/auth/register', data)
  },

  logout() {
    return request.post('/auth/logout')
  },

  getUserInfo() {
    return request.get('/auth/info')
  },

  updateUserInfo(data) {
    return request.put('/auth/info', data)
  },

  updatePassword(data) {
    return request.put('/auth/password', data)
  }
}
