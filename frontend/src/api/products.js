import http from './http'

export function list(params) {
  return http.get('/products', { params })
}

export function detail(id) {
  return http.get(`/products/${id}`)
}

export function create(data) {
  return http.post('/products', data)
}

export function update(id, data) {
  return http.put(`/products/${id}`, data)
}

export function patchStatus(id, data) {
  return http.patch(`/products/${id}/status`, data)
}

export function mine(params) {
  return http.get('/products/me', { params })
}


export function remove(id) {
  return http.delete(`/products/${id}`)
}
