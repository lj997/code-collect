<template>
  <div class="home">
    <div class="container">
      <div class="home-header">
        <div class="search-bar">
          <input 
            type="text" 
            v-model="searchKeyword" 
            class="form-input"
            placeholder="搜索标题、描述或代码内容..."
            @keyup.enter="handleSearch"
          >
          <button class="btn btn-primary" @click="handleSearch">搜索</button>
        </div>
        
        <div class="filters">
          <div class="filter-section">
            <label class="filter-label">编程语言：</label>
            <select v-model="selectedLanguage" class="form-select filter-select" @change="filterByLanguage">
              <option value="">全部语言</option>
              <option v-for="lang in languages" :key="lang" :value="lang">{{ lang }}</option>
            </select>
          </div>
          
          <div class="filter-section">
            <label class="filter-label">分类：</label>
            <select v-model="selectedCategory" class="form-select filter-select" @change="filterByCategory">
              <option value="">全部分类</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>
          
          <div class="filter-section">
            <label class="filter-label">标签：</label>
            <div class="tag-selector">
              <span 
                v-for="tag in allTags" 
                :key="tag"
                class="tag-selector-item"
                :class="{ active: selectedTags.includes(tag) }"
                @click="toggleTag(tag)"
              >
                {{ tag }}
              </span>
            </div>
          </div>
        </div>
        
        <div class="actions">
          <button class="btn btn-secondary btn-sm" @click="handleExport">📥 导出</button>
          <label class="btn btn-secondary btn-sm import-btn">
            📤 导入
            <input type="file" accept=".json" @change="handleImport" hidden>
          </label>
        </div>
      </div>
      
      <div v-if="snippets.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3 class="empty-title">暂无代码片段</h3>
        <p class="empty-text">点击右上角"新建片段"开始添加你的第一个代码片段</p>
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
                class="snippet-action-btn"
                :class="{ starred: snippet.starred }"
                @click="toggleStar(snippet)"
                title="收藏"
              >
                {{ snippet.starred ? '⭐' : '☆' }}
              </button>
              <button 
                class="snippet-action-btn edit"
                @click="editSnippet(snippet.id)"
                title="编辑"
              >
                ✏️
              </button>
              <button 
                class="snippet-action-btn delete"
                @click="handleDelete(snippet)"
                title="删除"
              >
                🗑️
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
import { 
  getAllSnippets, 
  searchSnippets, 
  getSnippetsByLanguage, 
  getSnippetsByCategory,
  getSnippetsByTags,
  getUsedLanguages,
  toggleStar,
  deleteSnippet
} from '../api/snippets'
import { getAllCategories } from '../api/categories'
import { getAllTags } from '../api/tags'
import { exportSnippets, importSnippets } from '../api/importExport'

const router = useRouter()

const snippets = ref([])
const languages = ref([])
const categories = ref([])
const allTags = ref([])

const searchKeyword = ref('')
const selectedLanguage = ref('')
const selectedCategory = ref('')
const selectedTags = ref([])

async function fetchSnippets() {
  try {
    snippets.value = await getAllSnippets()
  } catch (error) {
    console.error('获取代码片段失败:', error)
  }
}

async function fetchLanguages() {
  try {
    languages.value = await getUsedLanguages()
  } catch (error) {
    console.error('获取语言列表失败:', error)
  }
}

async function fetchCategories() {
  try {
    categories.value = await getAllCategories()
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

async function fetchTags() {
  try {
    allTags.value = await getAllTags()
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

async function handleSearch() {
  if (searchKeyword.value.trim()) {
    try {
      snippets.value = await searchSnippets(searchKeyword.value.trim())
    } catch (error) {
      console.error('搜索失败:', error)
    }
  } else {
    fetchSnippets()
  }
}

async function filterByLanguage() {
  resetOtherFilters('language')
  if (selectedLanguage.value) {
    try {
      snippets.value = await getSnippetsByLanguage(selectedLanguage.value)
    } catch (error) {
      console.error('按语言筛选失败:', error)
    }
  } else {
    fetchSnippets()
  }
}

async function filterByCategory() {
  resetOtherFilters('category')
  if (selectedCategory.value) {
    try {
      snippets.value = await getSnippetsByCategory(selectedCategory.value)
    } catch (error) {
      console.error('按分类筛选失败:', error)
    }
  } else {
    fetchSnippets()
  }
}

function toggleTag(tag) {
  resetOtherFilters('tags')
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
  
  if (selectedTags.value.length > 0) {
    filterByTags()
  } else {
    fetchSnippets()
  }
}

async function filterByTags() {
  try {
    snippets.value = await getSnippetsByTags(selectedTags.value)
  } catch (error) {
    console.error('按标签筛选失败:', error)
  }
}

function resetOtherFilters(exclude) {
  if (exclude !== 'language') {
    selectedLanguage.value = ''
  }
  if (exclude !== 'category') {
    selectedCategory.value = ''
  }
  if (exclude !== 'tags') {
    selectedTags.value = []
  }
  if (exclude !== 'search') {
    searchKeyword.value = ''
  }
}

async function toggleStar(snippet) {
  try {
    await toggleStar(snippet.id)
    snippet.starred = !snippet.starred
  } catch (error) {
    console.error('切换收藏状态失败:', error)
  }
}

function viewSnippet(id) {
  router.push(`/snippets/${id}`)
}

function editSnippet(id) {
  router.push(`/snippets/${id}/edit`)
}

async function handleDelete(snippet) {
  if (confirm(`确定要删除代码片段 "${snippet.title}" 吗？`)) {
    try {
      await deleteSnippet(snippet.id)
      snippets.value = snippets.value.filter(s => s.id !== snippet.id)
    } catch (error) {
      console.error('删除失败:', error)
      alert('删除失败')
    }
  }
}

async function handleExport() {
  try {
    await exportSnippets()
  } catch (error) {
    console.error('导出失败:', error)
    alert('导出失败')
  }
}

async function handleImport(event) {
  const file = event.target.files[0]
  if (!file) return
  
  try {
    const result = await importSnippets(file)
    if (result.success) {
      alert(`成功导入 ${result.importedCount} 个代码片段`)
      fetchSnippets()
    }
  } catch (error) {
    console.error('导入失败:', error)
    alert('导入失败: ' + (error.response?.data?.message || '未知错误'))
  }
  
  event.target.value = ''
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchSnippets()
  fetchLanguages()
  fetchCategories()
  fetchTags()
})
</script>

<style scoped>
.home {
  min-height: 100%;
}

.home-header {
  margin-bottom: 24px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.search-bar input {
  flex: 1;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
  align-items: center;
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.filter-select {
  min-width: 150px;
}

.tag-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag-selector-item {
  padding: 4px 10px;
  background-color: var(--bg-tertiary);
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.tag-selector-item:hover {
  background-color: var(--primary-color);
  color: white;
}

.tag-selector-item.active {
  background-color: var(--primary-color);
  color: white;
}

.actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.import-btn {
  cursor: pointer;
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
