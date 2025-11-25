import http from './http'

export function create(data) {
  return http.post('/orders', data)
}

export function cancel(id) {
  return http.post(`/orders/${id}/cancel`)
}

export function ship(id, data) {
  return http.patch(`/orders/${id}/ship`, data)
}

export function confirm(id) {
  return http.patch(`/orders/${id}/confirm`)
}

export function bought(params) {
  return http.get('/orders/me/bought', { params })
}

export function sold(params) {
  return http.get('/orders/me/sold', { params })
}

export function precheck(params) {
  return http.get('/orders/precheck', { params })
}
