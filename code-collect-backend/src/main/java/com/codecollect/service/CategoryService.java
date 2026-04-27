package com.codecollect.service;

import com.codecollect.dto.CategoryDTO;
import com.codecollect.entity.Category;
import com.codecollect.entity.User;
import com.codecollect.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<CategoryDTO> getAllCategories(User user) {
        List<Category> categories = categoryRepository.findByUserOrderByNameAsc(user);
        return categories.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    public CategoryDTO getCategoryById(User user, Long id) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        return toDTO(category);
    }
    
    @Transactional
    public CategoryDTO createCategory(User user, CategoryDTO dto) {
        if (categoryRepository.existsByNameAndUser(dto.getName(), user)) {
            throw new RuntimeException("分类名称已存在");
        }
        
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setUser(user);
        
        category = categoryRepository.save(category);
        return toDTO(category);
    }
    
    @Transactional
    public CategoryDTO updateCategory(User user, Long id, CategoryDTO dto) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        if (!category.getName().equals(dto.getName()) && 
            categoryRepository.existsByNameAndUser(dto.getName(), user)) {
            throw new RuntimeException("分类名称已存在");
        }
        
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        
        category = categoryRepository.save(category);
        return toDTO(category);
    }
    
    @Transactional
    public void deleteCategory(User user, Long id) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        if (category.getSnippets() != null && !category.getSnippets().isEmpty()) {
            throw new RuntimeException("分类下还有代码片段，无法删除");
        }
        
        categoryRepository.delete(category);
    }
    
    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}