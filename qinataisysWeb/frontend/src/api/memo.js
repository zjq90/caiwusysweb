import request from '@/utils/request'

export const memoApi = {
  getMyMemos() {
    return request.get('/memos/my')
  },

  getList(params) {
    return request.get('/memos', { params })
  },

  getDetail(id) {
    return request.get(`/memos/${id}`)
  },

  add(data) {
    return request.post('/memos', data)
  },

  update(id, data) {
    return request.put(`/memos/${id}`, data)
  },

  delete(id) {
    return request.delete(`/memos/${id}`)
  }
}
