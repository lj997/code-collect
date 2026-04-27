<template>
  <div class="starred">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">⭐ 收藏的代码片段</h1>
      </div>
      
      <div v-if="snippets.length === 0" class="empty-state">
        <div class="empty-icon">⭐</div>
        <h3 class="empty-title">暂无收藏的代码片段</h3>
        <p class="empty-text">在代码片段列表中点击星标图标来收藏你喜欢的片段</p>
      </div>
      
      <div v-else class="snippet-list">
        <div 
          v-for="snippet in snippets" 
          :key="snippet.id"
          class="snippet-card"
          @click="viewSnippet(snippet.id)"
        >
          <div class="snippet-header">
            <h3 class="snippet-title">{{ snippet.title }}</h3>
            <div class="snippet-actions" @click.stop>
              <button 
                class="snippet-action-btn starred"
                @click="handleToggleStar(snippet)"
                title="取消收藏"
              >
                ⭐
              </button>
              <button 
                class="snippet-action-btn edit"
                @click="editSnippet(snippet.id)"
                title="编辑"
              >
                ✏️
              </button>
            </div>
          </div>
          
          <p v-if="snippet.description" class="snippet-description">{{ snippet.description }}</p>
          
          <div class="snippet-meta">
            <span class="badge badge-info">{{ snippet.programmingLanguage }}</span>
            <span v-if="snippet.categoryName" class="badge badge-warning">📁 {{ snippet.categoryName }}</span>
            <span v-if="snippet.viewCount" class="text-muted">👁️ {{ snippet.viewCount }} 次浏览</span>
          </div>
          
          <div v-if="snippet.tags && snippet.tags.length > 0" class="snippet-tags">
            <span v-for="tag in snippet.tags" :key="tag" class="tag primary">{{ tag }}</span>
          </div>
          
          <div class="snippet-footer">
            <span class="snippet-date">创建于 {{ formatDate(snippet.createdAt) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getStarredSnippets, toggleStar } from '../api/snippets'

const router = useRouter()

const snippets = ref([])

async function fetchSnippets() {
  try {
    snippets.value = await getStarredSnippets()
  } catch (error) {
    console.error('获取收藏列表失败:', error)
  }
}

async function handleToggleStar(snippet) {
  try {
    await toggleStar(snippet.id)
    snippets.value = snippets.value.filter(s => s.id !== snippet.id)
  } catch (error) {
    console.error('取消收藏失败:', error)
  }
}

function viewSnippet(id) {
  router.push(`/snippets/${id}`)
}

function editSnippet(id) {
  router.push(`/snippets/${id}/edit`)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchSnippets()
})
</script>

<style scoped>
.starred {
  min-height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
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

.snippet-list {
  display: grid;
  gap: 16px;
}

.snippet-card {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.snippet-card:hover {
  border-color: var(--primary-color);
}

.snippet-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.snippet-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.snippet-actions {
  display: flex;
  gap: 8px;
}

.snippet-action-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-size: 16px;
  transition: all 0.2s;
}

.snippet-action-btn:hover {
  background-color: var(--bg-tertiary);
}

.snippet-action-btn.starred {
  color: #fbbf24;
}

.snippet-description {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 12px;
}

.snippet-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
  align-items: center;
}

.snippet-tags {
  margin-bottom: 12px;
}

.snippet-footer {
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
}

.snippet-date {
  font-size: 12px;
  color: var(--text-muted);
}
</style>
