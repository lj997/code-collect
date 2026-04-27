<template>
  <div class="snippet-form">
    <div class="container">
      <div class="form-header">
        <h1 class="form-title">{{ isEdit ? '编辑代码片段' : '新建代码片段' }}</h1>
      </div>
      
      <div class="form-card">
        <form @submit.prevent="handleSubmit">
          <div class="form-row">
            <div class="form-group col-full">
              <label class="form-label">标题 <span class="required">*</span></label>
              <input 
                type="text" 
                v-model="form.title" 
                class="form-input"
                placeholder="输入代码片段的标题"
                required
              >
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group col-full">
              <label class="form-label">描述</label>
              <textarea 
                v-model="form.description" 
                class="form-textarea"
                placeholder="输入代码片段的描述（可选）"
                rows="3"
              ></textarea>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group col-full">
              <label class="form-label">代码内容 <span class="required">*</span></label>
              <textarea 
                v-model="form.code" 
                class="form-textarea code-textarea"
                placeholder="粘贴或输入你的代码..."
                rows="15"
                required
              ></textarea>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group col-half">
              <label class="form-label">编程语言 <span class="required">*</span></label>
              <select v-model="form.programmingLanguage" class="form-select" required>
                <option value="">请选择编程语言</option>
                <option value="Java">Java</option>
                <option value="Python">Python</option>
                <option value="JavaScript">JavaScript</option>
                <option value="TypeScript">TypeScript</option>
                <option value="Go">Go</option>
                <option value="Rust">Rust</option>
                <option value="C">C</option>
                <option value="C++">C++</option>
                <option value="C#">C#</option>
                <option value="PHP">PHP</option>
                <option value="Ruby">Ruby</option>
                <option value="Swift">Swift</option>
                <option value="Kotlin">Kotlin</option>
                <option value="SQL">SQL</option>
                <option value="Shell">Shell</option>
                <option value="HTML">HTML</option>
                <option value="CSS">CSS</option>
                <option value="JSON">JSON</option>
                <option value="YAML">YAML</option>
                <option value="Markdown">Markdown</option>
                <option value="Other">Other</option>
              </select>
            </div>
            
            <div class="form-group col-half">
              <label class="form-label">分类</label>
              <select v-model="form.categoryId" class="form-select">
                <option value="">无分类</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
              </select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group col-full">
              <label class="form-label">标签（用逗号分隔）</label>
              <input 
                type="text" 
                v-model="tagsInput" 
                class="form-input"
                placeholder="例如：算法, 数据结构, 面试"
              >
              <div v-if="selectedTags.length > 0" class="selected-tags">
                <span 
                  v-for="tag in selectedTags" 
                  :key="tag"
                  class="tag primary"
                >
                  {{ tag }}
                  <button type="button" class="tag-remove" @click="removeTag(tag)">×</button>
                </span>
              </div>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group col-full">
              <label class="form-checkbox">
                <input type="checkbox" v-model="form.starred">
                <span>添加到收藏</span>
              </label>
            </div>
          </div>
          
          <div class="form-actions">
            <button type="button" class="btn btn-secondary" @click="goBack">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{ loading ? '保存中...' : (isEdit ? '更新' : '创建') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createSnippet, updateSnippet, getSnippetById } from '../api/snippets'
import { getAllCategories } from '../api/categories'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)

const form = ref({
  title: '',
  description: '',
  code: '',
  programmingLanguage: '',
  categoryId: null,
  tags: [],
  starred: false,
  archived: false
})

const categories = ref([])
const tagsInput = ref('')
const selectedTags = ref([])
const loading = ref(false)

function goBack() {
  router.back()
}

async function fetchCategories() {
  try {
    categories.value = await getAllCategories()
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

async function fetchSnippet() {
  if (!isEdit.value) return
  
  try {
    const snippet = await getSnippetById(route.params.id)
    form.value = {
      title: snippet.title,
      description: snippet.description || '',
      code: snippet.code,
      programmingLanguage: snippet.programmingLanguage,
      categoryId: snippet.categoryId || null,
      tags: snippet.tags || [],
      starred: snippet.starred,
      archived: snippet.archived
    }
    selectedTags.value = [...(snippet.tags || [])]
  } catch (error) {
    console.error('获取代码片段失败:', error)
    router.push('/')
  }
}

watch(tagsInput, (newVal) => {
  if (newVal.includes(',')) {
    const parts = newVal.split(',')
    const lastPart = parts.pop().trim()
    parts.forEach(part => {
      const tag = part.trim()
      if (tag && !selectedTags.value.includes(tag)) {
        selectedTags.value.push(tag)
      }
    })
    tagsInput.value = lastPart
  }
})

function removeTag(tag) {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  }
}

async function handleSubmit() {
  if (tagsInput.value.trim()) {
    selectedTags.value.push(tagsInput.value.trim())
    tagsInput.value = ''
  }
  
  form.value.tags = [...selectedTags.value]
  
  loading.value = true
  
  try {
    if (isEdit.value) {
      await updateSnippet(route.params.id, form.value)
    } else {
      await createSnippet(form.value)
    }
    router.push('/')
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCategories()
  fetchSnippet()
})
</script>

<style scoped>
.snippet-form {
  min-height: 100%;
}

.form-header {
  margin-bottom: 24px;
}

.form-title {
  font-size: 24px;
  font-weight: 600;
}

.form-card {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 24px;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.col-full {
  flex: 1;
}

.col-half {
  flex: 0.5;
}

.code-textarea {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  tab-size: 4;
}

.required {
  color: var(--danger-color);
}

.form-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-secondary);
}

.form-checkbox input {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.tag-remove {
  background: none;
  border: none;
  color: inherit;
  font-size: 16px;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.tag-remove:hover {
  color: var(--danger-color);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
  }
  
  .col-half {
    flex: 1;
  }
}
</style>
