import http from './http'

export function upload(formData) {
  return http.post('/files/upload', formData)
}
