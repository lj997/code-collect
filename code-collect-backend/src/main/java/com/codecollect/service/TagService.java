package com.codecollect.service;

import com.codecollect.entity.Tag;
import com.codecollect.entity.User;
import com.codecollect.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;
    
    public List<String> getAllTags(User user) {
        List<Tag> tags = tagRepository.findByUserOrderByNameAsc(user);
        return tags.stream().map(Tag::getName).collect(Collectors.toList());
    }
    
    public void deleteTag(User user, String name) {
        Tag tag = tagRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        
        if (tag.getSnippets() != null && !tag.getSnippets().isEmpty()) {
            throw new RuntimeException("标签下还有代码片段，无法删除");
        }
        
        tagRepository.delete(tag);
    }
}