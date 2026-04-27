package com.codecollect.controller;

import com.codecollect.entity.User;
import com.codecollect.security.CustomUserDetailsService;
import com.codecollect.security.UserPrincipal;
import com.codecollect.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @GetMapping
    public List<String> getAllTags(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userDetailsService.getCurrentUser(userPrincipal);
        return tagService.getAllTags(user);
    }
    
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteTag(
            @PathVariable String name,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userDetailsService.getCurrentUser(userPrincipal);
            tagService.deleteTag(user, name);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}