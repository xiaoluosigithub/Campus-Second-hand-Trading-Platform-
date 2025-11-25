import http from './http'

export function mock(orderId, method) {
  return http.post(`/pay/mock?orderId=${orderId}&method=${method}`)
}
