package com.codecollect.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SnippetResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String code;
    private String programmingLanguage;
    private Long categoryId;
    private String categoryName;
    private List<String> tags;
    private boolean starred;
    private boolean archived;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}