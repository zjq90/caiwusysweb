import request from '@/utils/request'

export const categoryApi = {
  getIncomeList() {
    return request.get('/categories/income')
  },

  getExpenseList() {
    return request.get('/categories/expense')
  },

  getByType(type) {
    return request.get(`/categories/type/${type}`)
  },

  getDetail(id) {
    return request.get(`/categories/${id}`)
  },

  add(data) {
    return request.post('/categories', data)
  },

  update(id, data) {
    return request.put(`/categories/${id}`, data)
  },

  delete(id) {
    return request.delete(`/categories/${id}`)
  }
}
