import api from './index'

export function getAllSnippets() {
  return api.get('/snippets').then(response => response.data)
}

export function getStarredSnippets() {
  return api.get('/snippets/starred').then(response => response.data)
}

export function getSnippetsByLanguage(language) {
  return api.get(`/snippets/language/${language}`).then(response => response.data)
}

export function getSnippetsByCategory(categoryId) {
  return api.get(`/snippets/category/${categoryId}`).then(response => response.data)
}

export function searchSnippets(keyword) {
  return api.get('/snippets/search', { params: { keyword } }).then(response => response.data)
}

export function getSnippetsByTags(tagIds) {
  return api.get('/snippets/tags', { params: { tagIds } }).then(response => response.data)
}

export function getUsedLanguages() {
  return api.get('/snippets/languages').then(response => response.data)
}

export function getSnippetById(id) {
  return api.get(`/snippets/${id}`).then(response => response.data)
}

export function createSnippet(data) {
  return api.post('/snippets', data).then(response => response.data)
}

export function updateSnippet(id, data) {
  return api.put(`/snippets/${id}`, data).then(response => response.data)
}

export function toggleStar(id) {
  return api.put(`/snippets/${id}/star`)
}

export function toggleArchive(id) {
  return api.put(`/snippets/${id}/archive`)
}

export function deleteSnippet(id) {
  return api.delete(`/snippets/${id}`)
}
