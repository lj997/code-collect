package com.codecollect.controller;

import com.codecollect.dto.SnippetDTO;
import com.codecollect.dto.SnippetResponseDTO;
import com.codecollect.entity.User;
import com.codecollect.security.CustomUserDetailsService;
import com.codecollect.security.UserPrincipal;
import com.codecollect.service.SnippetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/snippets")
public class SnippetController {
    @Autowired
    private SnippetService snippetService;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @GetMapping
    public List<SnippetResponseDTO> getAllSnippets(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userDetailsService.getCurrentUser(userPrincipal);
        return snippetService.getAllSnippets(user);
    }
    
    @GetMapping("/starred")
    public List<SnippetResponseDTO> getStarredSnippets(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userDetailsService.getCurrentUser(userPrincipal);
        return snippetService.getStarredSnippets(user);
    }
    
    @GetMapping("/language/{language}")
    public List<SnippetResponseDTO> getSnippetsByLanguage(
            @PathVariable String language,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userDetailsService.getCurrentUser(userPrincipal);
        return snippetService.getSnippetsByLanguage(user, language);
    }
    
    @GetMapping("/category/{categoryId}")
    public List<SnippetResponseDTO> getSnippetsByCategory(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userDetailsService.getCurrentUser(userPrincipal);
        return snippetService.getSnippetsByCategory(user, categoryId);
    }
    
    @GetMapping("/search")
    public List<SnippetResponseDTO> searchSnippets(
            @RequestParam String keyword,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userDetailsService.getCurrentUser(userPrincipal);
        return snippetService.searchSnippets(user, keyword);
    }
    
    @GetMapping("/tags")
    public List<SnippetResponseDTO> getSnippetsByTags(
            @RequestParam List<Long> tagIds,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userDetailsService.getCurrentUser(userPrincipal);
        return snippetService.getSnippetsByTags(user, tagIds);
    }
    
    @GetMapping("/languages")
    public List<String> getUsedLanguages(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userDetailsService.getCurrentUser(userPrincipal);
        return snippetService.getUsedLanguages(user);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SnippetResponseDTO> getSnippetById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userDetailsService.getCurrentUser(userPrincipal);
            SnippetResponseDTO snippet = snippetService.getSnippetById(user, id);
            return ResponseEntity.ok(snippet);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<SnippetResponseDTO> createSnippet(
            @Valid @RequestBody SnippetDTO dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userDetailsService.getCurrentUser(userPrincipal);
        SnippetResponseDTO created = snippetService.createSnippet(user, dto);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SnippetResponseDTO> updateSnippet(
            @PathVariable Long id,
            @Valid @RequestBody SnippetDTO dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userDetailsService.getCurrentUser(userPrincipal);
            SnippetResponseDTO updated = snippetService.updateSnippet(user, id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/star")
    public ResponseEntity<Void> toggleStar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userDetailsService.getCurrentUser(userPrincipal);
            snippetService.toggleStar(user, id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> toggleArchive(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userDetailsService.getCurrentUser(userPrincipal);
            snippetService.toggleArchive(user, id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnippet(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userDetailsService.getCurrentUser(userPrincipal);
            snippetService.deleteSnippet(user, id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}