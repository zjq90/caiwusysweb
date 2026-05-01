import request from '@/utils/request'

export function getCategoryListApi(params) {
  return request({
    url: '/category/list',
    method: 'get',
    params
  })
}

export function getCategoryByTypeApi(type) {
  return request({
    url: `/category/listByType/${type}`,
    method: 'get'
  })
}

export function getCategoryByIdApi(id) {
  return request({
    url: `/category/get/${id}`,
    method: 'get'
  })
}

export function addCategoryApi(data) {
  return request({
    url: '/category/add',
    method: 'post',
    data
  })
}

export function updateCategoryApi(data) {
  return request({
    url: '/category/update',
    method: 'post',
    data
  })
}

export function deleteCategoryApi(id) {
  return request({
    url: `/category/delete/${id}`,
    method: 'post'
  })
}

export function batchDeleteCategoryApi(ids) {
  return request({
    url: '/category/batchDelete',
    method: 'post',
    data: ids
  })
}
