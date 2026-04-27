package com.codecollect.controller;

import com.codecollect.dto.CategoryDTO;
import com.codecollect.entity.User;
import com.codecollect.security.CustomUserDetailsService;
import com.codecollect.security.UserPrincipal;
import com.codecollect.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @GetMapping
    public List<CategoryDTO> getAllCategories(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userDetailsService.getCurrentUser(userPrincipal);
        return categoryService.getAllCategories(user);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userDetailsService.getCurrentUser(userPrincipal);
            CategoryDTO category = categoryService.getCategoryById(user, id);
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CategoryDTO dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userDetailsService.getCurrentUser(userPrincipal);
            CategoryDTO created = categoryService.createCategory(user, dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userDetailsService.getCurrentUser(userPrincipal);
            CategoryDTO updated = categoryService.updateCategory(user, id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userDetailsService.getCurrentUser(userPrincipal);
            categoryService.deleteCategory(user, id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}