import request from '@/utils/request'

export const newsApi = {
  getList(params) {
    return request.get('/news', { params })
  },

  getDetail(id) {
    return request.get(`/news/${id}`)
  }
}
