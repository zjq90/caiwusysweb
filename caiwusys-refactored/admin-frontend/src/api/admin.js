import request from '@/utils/request'

export function loginApi(username, password) {
  const formData = new FormData()
  formData.append('username', username)
  formData.append('password', password)
  return request({
    url: '/admin/login',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getAdminInfoApi() {
  return request({
    url: '/admin/info',
    method: 'get'
  })
}

export function getAdminListApi(params) {
  return request({
    url: '/admin/list',
    method: 'get',
    params
  })
}

export function getAdminByIdApi(id) {
  return request({
    url: `/admin/get/${id}`,
    method: 'get'
  })
}

export function addAdminApi(data) {
  return request({
    url: '/admin/add',
    method: 'post',
    data
  })
}

export function updateAdminApi(data) {
  return request({
    url: '/admin/update',
    method: 'post',
    data
  })
}

export function deleteAdminApi(id) {
  return request({
    url: `/admin/delete/${id}`,
    method: 'post'
  })
}

export function batchDeleteAdminApi(ids) {
  return request({
    url: '/admin/batchDelete',
    method: 'post',
    data: ids
  })
}
