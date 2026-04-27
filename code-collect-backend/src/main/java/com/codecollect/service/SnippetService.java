package com.codecollect.service;

import com.codecollect.dto.SnippetDTO;
import com.codecollect.dto.SnippetResponseDTO;
import com.codecollect.entity.Category;
import com.codecollect.entity.Snippet;
import com.codecollect.entity.Tag;
import com.codecollect.entity.User;
import com.codecollect.repository.CategoryRepository;
import com.codecollect.repository.SnippetRepository;
import com.codecollect.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SnippetService {
    @Autowired
    private SnippetRepository snippetRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    public List<SnippetResponseDTO> getAllSnippets(User user) {
        List<Snippet> snippets = snippetRepository.findByUserAndArchivedFalseOrderByCreatedAtDesc(user);
        return snippets.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
    
    public List<SnippetResponseDTO> getStarredSnippets(User user) {
        List<Snippet> snippets = snippetRepository.findByUserAndStarredTrueAndArchivedFalseOrderByCreatedAtDesc(user);
        return snippets.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
    
    public List<SnippetResponseDTO> getSnippetsByLanguage(User user, String language) {
        List<Snippet> snippets = snippetRepository.findByUserAndProgrammingLanguageAndArchivedFalseOrderByCreatedAtDesc(user, language);
        return snippets.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
    
    public List<SnippetResponseDTO> getSnippetsByCategory(User user, Long categoryId) {
        List<Snippet> snippets = snippetRepository.findByUserAndCategoryIdAndArchivedFalseOrderByCreatedAtDesc(user, categoryId);
        return snippets.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
    
    public List<SnippetResponseDTO> searchSnippets(User user, String keyword) {
        List<Snippet> snippets = snippetRepository.searchByKeyword(user, keyword);
        return snippets.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
    
    public List<SnippetResponseDTO> getSnippetsByTags(User user, List<Long> tagIds) {
        List<Snippet> snippets = snippetRepository.findByTagIds(user, tagIds);
        return snippets.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
    
    public List<String> getUsedLanguages(User user) {
        return snippetRepository.findDistinctProgrammingLanguagesByUser(user);
    }
    
    public SnippetResponseDTO getSnippetById(User user, Long id) {
        Snippet snippet = snippetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("代码片段不存在"));
        
        snippet.setViewCount(snippet.getViewCount() + 1);
        snippet = snippetRepository.save(snippet);
        
        return toResponseDTO(snippet);
    }
    
    @Transactional
    public SnippetResponseDTO createSnippet(User user, SnippetDTO dto) {
        Snippet snippet = new Snippet();
        snippet.setTitle(dto.getTitle());
        snippet.setDescription(dto.getDescription());
        snippet.setCode(dto.getCode());
        snippet.setProgrammingLanguage(dto.getProgrammingLanguage());
        snippet.setUser(user);
        snippet.setStarred(dto.isStarred());
        snippet.setArchived(dto.isArchived());
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUser(dto.getCategoryId(), user)
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            snippet.setCategory(category);
        }
        
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            Set<Tag> tags = processTags(user, dto.getTags());
            snippet.setTags(tags);
        }
        
        snippet = snippetRepository.save(snippet);
        return toResponseDTO(snippet);
    }
    
    @Transactional
    public SnippetResponseDTO updateSnippet(User user, Long id, SnippetDTO dto) {
        Snippet snippet = snippetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("代码片段不存在"));
        
        snippet.setTitle(dto.getTitle());
        snippet.setDescription(dto.getDescription());
        snippet.setCode(dto.getCode());
        snippet.setProgrammingLanguage(dto.getProgrammingLanguage());
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUser(dto.getCategoryId(), user)
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            snippet.setCategory(category);
        } else {
            snippet.setCategory(null);
        }
        
        if (dto.getTags() != null) {
            Set<Tag> tags = processTags(user, dto.getTags());
            snippet.setTags(tags);
        }
        
        snippet = snippetRepository.save(snippet);
        return toResponseDTO(snippet);
    }
    
    @Transactional
    public void toggleStar(User user, Long id) {
        Snippet snippet = snippetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("代码片段不存在"));
        snippet.setStarred(!snippet.isStarred());
        snippetRepository.save(snippet);
    }
    
    @Transactional
    public void toggleArchive(User user, Long id) {
        Snippet snippet = snippetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("代码片段不存在"));
        snippet.setArchived(!snippet.isArchived());
        snippetRepository.save(snippet);
    }
    
    @Transactional
    public void deleteSnippet(User user, Long id) {
        Snippet snippet = snippetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("代码片段不存在"));
        snippetRepository.delete(snippet);
    }
    
    private Set<Tag> processTags(User user, List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        
        for (String name : tagNames) {
            Optional<Tag> existingTag = tagRepository.findByNameAndUser(name.trim(), user);
            if (existingTag.isPresent()) {
                tags.add(existingTag.get());
            } else {
                Tag newTag = new Tag();
                newTag.setName(name.trim());
                newTag.setUser(user);
                newTag = tagRepository.save(newTag);
                tags.add(newTag);
            }
        }
        
        return tags;
    }
    
    private SnippetResponseDTO toResponseDTO(Snippet snippet) {
        SnippetResponseDTO dto = new SnippetResponseDTO();
        dto.setId(snippet.getId());
        dto.setTitle(snippet.getTitle());
        dto.setDescription(snippet.getDescription());
        dto.setCode(snippet.getCode());
        dto.setProgrammingLanguage(snippet.getProgrammingLanguage());
        dto.setStarred(snippet.isStarred());
        dto.setArchived(snippet.isArchived());
        dto.setViewCount(snippet.getViewCount());
        dto.setCreatedAt(snippet.getCreatedAt());
        dto.setUpdatedAt(snippet.getUpdatedAt());
        
        if (snippet.getCategory() != null) {
            dto.setCategoryId(snippet.getCategory().getId());
            dto.setCategoryName(snippet.getCategory().getName());
        }
        
        if (snippet.getTags() != null) {
            dto.setTags(snippet.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}