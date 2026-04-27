import api from './index'

export function getAllTags() {
  return api.get('/tags').then(response => response.data)
}

export function deleteTag(name) {
  return api.delete(`/tags/${name}`)
}
