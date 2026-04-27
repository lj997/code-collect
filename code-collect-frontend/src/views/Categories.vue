<template>
  <div class="categories">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">分类管理</h1>
        <button class="btn btn-primary" @click="showCreateModal = true">+ 新建分类</button>
      </div>
      
      <div v-if="categories.length === 0" class="empty-state">
        <div class="empty-icon">📁</div>
        <h3 class="empty-title">暂无分类</h3>
        <p class="empty-text">点击上方"新建分类"创建你的第一个分类</p>
      </div>
      
      <div v-else class="categories-grid">
        <div v-for="category in categories" :key="category.id" class="category-card">
          <div class="category-header">
            <h3 class="category-name">{{ category.name }}</h3>
            <div class="category-actions">
              <button class="action-btn" @click="startEdit(category)" title="编辑">✏️</button>
              <button class="action-btn" @click="handleDelete(category)" title="删除">🗑️</button>
            </div>
          </div>
          <p v-if="category.description" class="category-description">{{ category.description }}</p>
          <button 
            class="btn btn-sm btn-outline"
            @click="viewByCategory(category.id)"
          >
            查看相关片段
          </button>
        </div>
      </div>
    </div>
    
    <div v-if="showCreateModal || showEditModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h3 class="modal-title">{{ showEditModal ? '编辑分类' : '新建分类' }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">分类名称 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="form.name" 
              class="form-input"
              placeholder="输入分类名称"
              required
            >
          </div>
          <div class="form-group">
            <label class="form-label">描述</label>
            <textarea 
              v-model="form.description" 
              class="form-textarea"
              placeholder="输入分类描述（可选）"
              rows="3"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeModal">取消</button>
          <button class="btn btn-primary" @click="handleSubmit" :disabled="saving">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAllCategories, createCategory, updateCategory, deleteCategory } from '../api/categories'

const router = useRouter()

const categories = ref([])
const showCreateModal = ref(false)
const showEditModal = ref(false)
const editingId = ref(null)
const saving = ref(false)

const form = ref({
  name: '',
  description: ''
})

async function fetchCategories() {
  try {
    categories.value = await getAllCategories()
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

function startEdit(category) {
  editingId.value = category.id
  form.value = {
    name: category.name,
    description: category.description || ''
  }
  showEditModal.value = true
}

function closeModal() {
  showCreateModal.value = false
  showEditModal.value = false
  editingId.value = null
  form.value = {
    name: '',
    description: ''
  }
}

async function handleSubmit() {
  if (!form.value.name.trim()) {
    alert('请输入分类名称')
    return
  }
  
  saving.value = true
  
  try {
    if (showEditModal.value) {
      await updateCategory(editingId.value, form.value)
    } else {
      await createCategory(form.value)
    }
    await fetchCategories()
    closeModal()
  } catch (error) {
    console.error('保存分类失败:', error)
    alert(error.response?.data || '保存失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(category) {
  if (confirm(`确定要删除分类 "${category.name}" 吗？\n注意：分类下没有代码片段时才能删除。`)) {
    try {
      await deleteCategory(category.id)
      categories.value = categories.value.filter(c => c.id !== category.id)
    } catch (error) {
      console.error('删除分类失败:', error)
      alert(error.response?.data || '删除失败')
    }
  }
}

function viewByCategory(categoryId) {
  router.push('/')
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.categories {
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.category-card {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 20px;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.category-name {
  font-size: 18px;
  font-weight: 600;
}

.category-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-size: 16px;
  transition: all 0.2s;
}

.action-btn:hover {
  background-color: var(--bg-tertiary);
}

.category-description {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 16px;
}

.required {
  color: var(--danger-color);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-btn:hover {
  background-color: var(--bg-tertiary);
  color: var(--text-primary);
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid var(--border-color);
}

@media (max-width: 600px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .categories-grid {
    grid-template-columns: 1fr;
  }
}
</style>
