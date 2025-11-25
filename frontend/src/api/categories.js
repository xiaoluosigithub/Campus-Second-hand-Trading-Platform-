import http from './http'

export function tree() {
  return http.get('/categories/tree')
}

export function path(id) {
  return http.get(`/categories/${id}/path`)
}
