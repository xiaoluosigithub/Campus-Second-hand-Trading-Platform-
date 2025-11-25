import http from './http'

export function reviewProduct(id, data) {
  return http.patch(`/admin/products/${id}/review`, data)
}

export function categoriesList(params) {
  return http.get('/admin/categories', { params })
}

export function categoriesCreate(data) {
  return http.post('/admin/categories', data)
}

export function categoriesDelete(id) {
  return http.delete(`/admin/categories/${id}`)
}

export function usersList(params) {
  return http.get('/admin/users', { params })
}

export function userPatchRole(id, role) {
  return http.patch(`/admin/users/${id}/role`, null, { params: { role } })
}

export function userDelete(id) {
  return http.delete(`/admin/users/${id}`)
}

export function ordersList(params) {
  return http.get('/admin/orders', { params })
}

export function orderDetail(id) {
  return http.get(`/admin/orders/${id}`)
}

export function statsDay(params) {
  return http.get('/admin/stats/orders', { params })
}

export function statsRange(params) {
  return http.get('/admin/stats/orders-range', { params })
}
