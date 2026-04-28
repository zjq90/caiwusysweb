import request from '@/utils/request'

export const budgetApi = {
  getList(params) {
    return request.get('/budgets', { params })
  },

  getByMonth(year, month) {
    return request.get('/budgets/month', { params: { year, month } })
  },

  getDetail(id) {
    return request.get(`/budgets/${id}`)
  },

  add(data) {
    return request.post('/budgets', data)
  },

  update(id, data) {
    return request.put(`/budgets/${id}`, data)
  },

  delete(id) {
    return request.delete(`/budgets/${id}`)
  },

  getReport(year, month) {
    return request.get('/budgets/report', { params: { year, month } })
  }
}
