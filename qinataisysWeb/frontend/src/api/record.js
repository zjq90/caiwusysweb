import request from '@/utils/request'

export const recordApi = {
  getList(params) {
    return request.get('/records', { params })
  },

  getDetail(id) {
    return request.get(`/records/${id}`)
  },

  add(data) {
    return request.post('/records', data)
  },

  update(id, data) {
    return request.put(`/records/${id}`, data)
  },

  delete(id) {
    return request.delete(`/records/${id}`)
  },

  getMonthlySummary(month) {
    return request.get('/records/monthly/summary', { params: { month } })
  },

  getCategoryStatistics(type, month) {
    return request.get('/records/category/statistics', { params: { type, month } })
  },

  getDailyStatistics(month) {
    return request.get('/records/daily/statistics', { params: { month } })
  },

  getMonthlyStatistics(year) {
    return request.get('/records/monthly/statistics', { params: { year } })
  }
}
