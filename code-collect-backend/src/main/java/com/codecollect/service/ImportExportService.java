package com.codecollect.service;

import com.codecollect.dto.SnippetResponseDTO;
import com.codecollect.entity.Category;
import com.codecollect.entity.Snippet;
import com.codecollect.entity.Tag;
import com.codecollect.entity.User;
import com.codecollect.repository.CategoryRepository;
import com.codecollect.repository.SnippetRepository;
import com.codecollect.repository.TagRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImportExportService {
    @Autowired
    private SnippetRepository snippetRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    @Autowired
    private SnippetService snippetService;
    
    private final ObjectMapper objectMapper;
    
    public ImportExportService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    public byte[] exportSnippets(User user) throws IOException {
        List<Snippet> snippets = snippetRepository.findByUserAndArchivedFalseOrderByCreatedAtDesc(user);
        List<ExportSnippetDTO> exportDTOs = snippets.stream()
                .map(this::toExportDTO)
                .collect(Collectors.toList());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(baos, exportDTOs);
        return baos.toByteArray();
    }
    
    @Transactional
    public int importSnippets(User user, byte[] jsonData) throws IOException {
        String jsonString = new String(jsonData, StandardCharsets.UTF_8);
        List<ExportSnippetDTO> importDTOs = objectMapper.readValue(
                new ByteArrayInputStream(jsonData),
                new TypeReference<List<ExportSnippetDTO>>() {}
        );
        
        int count = 0;
        for (ExportSnippetDTO dto : importDTOs) {
            try {
                Snippet snippet = new Snippet();
                snippet.setTitle(dto.getTitle());
                snippet.setDescription(dto.getDescription());
                snippet.setCode(dto.getCode());
                snippet.setProgrammingLanguage(dto.getProgrammingLanguage());
                snippet.setUser(user);
                snippet.setStarred(dto.isStarred());
                snippet.setArchived(false);
                
                if (dto.getCategoryName() != null && !dto.getCategoryName().isEmpty()) {
                    Category category = getOrCreateCategory(user, dto.getCategoryName());
                    snippet.setCategory(category);
                }
                
                if (dto.getTags() != null && !dto.getTags().isEmpty()) {
                    Set<Tag> tags = getOrCreateTags(user, dto.getTags());
                    snippet.setTags(tags);
                }
                
                snippetRepository.save(snippet);
                count++;
            } catch (Exception e) {
                System.err.println("导入代码片段失败: " + dto.getTitle() + " - " + e.getMessage());
            }
        }
        
        return count;
    }
    
    private Category getOrCreateCategory(User user, String name) {
        return categoryRepository.findByNameAndUser(name, user)
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(name);
                    category.setUser(user);
                    return categoryRepository.save(category);
                });
    }
    
    private Set<Tag> getOrCreateTags(User user, List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            Tag tag = tagRepository.findByNameAndUser(name, user)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(name);
                        newTag.setUser(user);
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }
        return tags;
    }
    
    private ExportSnippetDTO toExportDTO(Snippet snippet) {
        ExportSnippetDTO dto = new ExportSnippetDTO();
        dto.setTitle(snippet.getTitle());
        dto.setDescription(snippet.getDescription());
        dto.setCode(snippet.getCode());
        dto.setProgrammingLanguage(snippet.getProgrammingLanguage());
        dto.setStarred(snippet.isStarred());
        
        if (snippet.getCategory() != null) {
            dto.setCategoryName(snippet.getCategory().getName());
        }
        
        if (snippet.getTags() != null) {
            dto.setTags(snippet.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public static class ExportSnippetDTO {
        private String title;
        private String description;
        private String code;
        private String programmingLanguage;
        private String categoryName;
        private List<String> tags;
        private boolean starred;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getProgrammingLanguage() { return programmingLanguage; }
        public void setProgrammingLanguage(String programmingLanguage) { this.programmingLanguage = programmingLanguage; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public boolean isStarred() { return starred; }
        public void setStarred(boolean starred) { this.starred = starred; }
    }
}