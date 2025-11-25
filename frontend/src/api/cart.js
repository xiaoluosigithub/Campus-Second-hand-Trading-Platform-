import http from './http'

export function add(data) {
  return http.post('/cart', data)
}

export function list(params) {
  return http.get('/cart', { params })
}

export function remove(productId) {
  return http.delete(`/cart/${productId}`)
}

export function cleanup() {
  return http.post('/cart/cleanup')
}

export function items(params) {
  return http.get('/cart/items', { params })
}
