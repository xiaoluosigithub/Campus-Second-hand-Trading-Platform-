import http from './http'

export function getMe() {
  return http.get('/users/me')
}

export function updateProfile(data) {
  return http.patch('/users/me/profile', data)
}
