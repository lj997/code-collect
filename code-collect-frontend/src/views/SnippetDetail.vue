<template>
  <div class="snippet-detail">
    <div class="container">
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="snippet" class="detail-content">
        <div class="detail-header">
          <div class="header-left">
            <button class="btn btn-sm btn-secondary" @click="goBack">← 返回</button>
          </div>
          <div class="header-right">
            <button 
              class="btn btn-sm"
              :class="snippet.starred ? 'btn-primary' : 'btn-outline'"
              @click="handleToggleStar"
            >
              {{ snippet.starred ? '⭐ 已收藏' : '☆ 收藏' }}
            </button>
            <button class="btn btn-sm btn-secondary" @click="handleEdit">✏️ 编辑</button>
            <button class="btn btn-sm btn-danger" @click="handleDelete">🗑️ 删除</button>
          </div>
        </div>
        
        <div class="detail-body">
          <h1 class="snippet-title">{{ snippet.title }}</h1>
          
          <div class="snippet-meta">
            <span class="badge badge-info">{{ snippet.programmingLanguage }}</span>
            <span v-if="snippet.categoryName" class="badge badge-warning">📁 {{ snippet.categoryName }}</span>
            <span class="text-muted">👁️ {{ snippet.viewCount }} 次浏览</span>
            <span class="text-muted">创建于 {{ formatDate(snippet.createdAt) }}</span>
            <span v-if="snippet.updatedAt && snippet.updatedAt !== snippet.createdAt" class="text-muted">
              更新于 {{ formatDate(snippet.updatedAt) }}
            </span>
          </div>
          
          <div v-if="snippet.description" class="snippet-description">
            {{ snippet.description }}
          </div>
          
          <div v-if="snippet.tags && snippet.tags.length > 0" class="snippet-tags">
            <span v-for="tag in snippet.tags" :key="tag" class="tag primary">{{ tag }}</span>
          </div>
          
          <div class="code-section">
            <div class="code-header">
              <span class="language-label">{{ snippet.programmingLanguage }}</span>
              <button class="btn btn-sm btn-secondary" @click="copyCode">
                {{ copied ? '✓ 已复制' : '📋 复制代码' }}
              </button>
            </div>
            <pre class="code-block"><code v-highlight :data-language="snippet.programmingLanguage">{{ snippet.code }}</code></pre>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-state">
        <div class="empty-icon">❓</div>
        <h3 class="empty-title">代码片段不存在</h3>
        <p class="empty-text">该代码片段可能已被删除或不存在</p>
        <button class="btn btn-primary mt-3" @click="goBack">返回列表</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSnippetById, toggleStar, deleteSnippet } from '../api/snippets'
import hljs from 'highlight.js'

const route = useRoute()
const router = useRouter()

const snippet = ref(null)
const loading = ref(true)
const copied = ref(false)

function goBack() {
  router.back()
}

async function fetchSnippet() {
  try {
    snippet.value = await getSnippetById(route.params.id)
  } catch (error) {
    console.error('获取代码片段失败:', error)
  } finally {
    loading.value = false
  }
}

async function handleToggleStar() {
  try {
    await toggleStar(snippet.value.id)
    snippet.value.starred = !snippet.value.starred
  } catch (error) {
    console.error('切换收藏状态失败:', error)
  }
}

function handleEdit() {
  router.push(`/snippets/${snippet.value.id}/edit`)
}

async function handleDelete() {
  if (confirm(`确定要删除代码片段 "${snippet.value.title}" 吗？`)) {
    try {
      await deleteSnippet(snippet.value.id)
      router.push('/')
    } catch (error) {
      console.error('删除失败:', error)
      alert('删除失败')
    }
  }
}

async function copyCode() {
  try {
    await navigator.clipboard.writeText(snippet.value.code)
    copied.value = true
    setTimeout(() => {
      copied.value = false
    }, 2000)
  } catch (error) {
    console.error('复制失败:', error)
    
    const textarea = document.createElement('textarea')
    textarea.value = snippet.value.code
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    
    copied.value = true
    setTimeout(() => {
      copied.value = false
    }, 2000)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchSnippet()
})
</script>

<style scoped>
.snippet-detail {
  min-height: 100%;
}

.loading {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-muted);
}

.detail-content {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border-color);
}

.header-right {
  display: flex;
  gap: 8px;
}

.detail-body {
  padding: 24px;
}

.snippet-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.snippet-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
  font-size: 14px;
}

.snippet-description {
  padding: 16px;
  background-color: var(--bg-tertiary);
  border-radius: 6px;
  margin-bottom: 20px;
  color: var(--text-secondary);
  line-height: 1.7;
}

.snippet-tags {
  margin-bottom: 24px;
}

.code-section {
  margin-top: 24px;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: var(--bg-tertiary);
  border-radius: 6px 6px 0 0;
}

.language-label {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
}

.code-block {
  margin: 0;
  padding: 0;
  border-radius: 0 0 6px 6px;
  overflow: auto;
  background-color: #282c34;
}

.code-block ::v-deep(pre) {
  margin: 0;
  padding: 16px;
  background: transparent;
}

.code-block ::v-deep(code) {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 20px;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.empty-text {
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .detail-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .header-right {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}
</style>
