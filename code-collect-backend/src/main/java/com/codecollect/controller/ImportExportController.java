package com.codecollect.controller;

import com.codecollect.entity.User;
import com.codecollect.security.CustomUserDetailsService;
import com.codecollect.security.UserPrincipal;
import com.codecollect.service.ImportExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/import-export")
public class ImportExportController {
    @Autowired
    private ImportExportService importExportService;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportSnippets(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            User user = userDetailsService.getCurrentUser(userPrincipal);
            byte[] data = importExportService.exportSnippets(user);
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "code-snippets_" + timestamp + ".json";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importSnippets(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            if (file.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "文件为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = userDetailsService.getCurrentUser(userPrincipal);
            int count = importExportService.importSnippets(user, file.getBytes());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("importedCount", count);
            response.put("message", "成功导入 " + count + " 个代码片段");
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "文件读取失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}