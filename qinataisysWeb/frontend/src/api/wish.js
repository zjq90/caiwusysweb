import request from '@/utils/request'

export const wishApi = {
  getMyWishes() {
    return request.get('/wishes/my')
  },

  getList(params) {
    return request.get('/wishes', { params })
  },

  getDetail(id) {
    return request.get(`/wishes/${id}`)
  },

  add(data) {
    return request.post('/wishes', data)
  },

  update(id, data) {
    return request.put(`/wishes/${id}`, data)
  },

  delete(id) {
    return request.delete(`/wishes/${id}`)
  }
}
