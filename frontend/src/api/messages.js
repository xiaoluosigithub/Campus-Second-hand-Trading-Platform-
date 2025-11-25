import http from './http'

export function contacts() {
  return http.get('/messages/contacts')
}

export function list(params) {
  return http.get('/messages', { params })
}

export function send(data) {
  return http.post('/messages', data)
}

export function markRead(id) {
  return http.post(`/messages/${id}/read`)
}

export function unreadCount() {
  return http.get('/messages/unread/count')
}
