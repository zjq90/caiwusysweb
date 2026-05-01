import request from '@/utils/request'

export function getNewsListApi(params) {
  return request({
    url: '/news/list',
    method: 'get',
    params
  })
}

export function getNewsByIdApi(id) {
  return request({
    url: `/news/get/${id}`,
    method: 'get'
  })
}

export function addNewsApi(data) {
  return request({
    url: '/news/add',
    method: 'post',
    data
  })
}

export function updateNewsApi(data) {
  return request({
    url: '/news/update',
    method: 'post',
    data
  })
}

export function deleteNewsApi(id) {
  return request({
    url: `/news/delete/${id}`,
    method: 'post'
  })
}

export function batchDeleteNewsApi(ids) {
  return request({
    url: '/news/batchDelete',
    method: 'post',
    data: ids
  })
}
