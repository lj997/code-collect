import api from './index'

export function exportSnippets() {
  return api.get('/import-export/export', {
    responseType: 'blob'
  }).then(response => {
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    const contentDisposition = response.headers['content-disposition']
    const filename = contentDisposition
      ? contentDisposition.split('filename=')[1]
      : 'code-snippets.json'
    link.setAttribute('download', filename)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    return { success: true }
  })
}

export function importSnippets(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return api.post('/import-export/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(response => response.data)
}
