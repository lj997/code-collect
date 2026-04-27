package com.codecollect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SnippetDTO {
    private Long id;
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    private String description;
    
    @NotBlank(message = "代码内容不能为空")
    private String code;
    
    @NotBlank(message = "编程语言不能为空")
    private String programmingLanguage;
    
    private Long categoryId;
    
    private List<String> tags;
    
    private boolean starred;
    
    private boolean archived;
}