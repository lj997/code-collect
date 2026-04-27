import api from './index'

export function getAllCategories() {
  return api.get('/categories').then(response => response.data)
}

export function getCategoryById(id) {
  return api.get(`/categories/${id}`).then(response => response.data)
}

export function createCategory(data) {
  return api.post('/categories', data).then(response => response.data)
}

export function updateCategory(id, data) {
  return api.put(`/categories/${id}`, data).then(response => response.data)
}

export function deleteCategory(id) {
  return api.delete(`/categories/${id}`)
}
