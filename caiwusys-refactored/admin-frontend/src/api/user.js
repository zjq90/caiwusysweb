import request from '@/utils/request'

export function getUserListApi(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

export function getUserByIdApi(id) {
  return request({
    url: `/user/get/${id}`,
    method: 'get'
  })
}

export function addUserApi(data) {
  return request({
    url: '/user/add',
    method: 'post',
    data
  })
}

export function updateUserApi(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}

export function deleteUserApi(id) {
  return request({
    url: `/user/delete/${id}`,
    method: 'post'
  })
}

export function batchDeleteUserApi(ids) {
  return request({
    url: '/user/batchDelete',
    method: 'post',
    data: ids
  })
}
