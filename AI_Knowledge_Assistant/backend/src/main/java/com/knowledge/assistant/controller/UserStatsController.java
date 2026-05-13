package com.knowledge.assistant.controller;

import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.entity.User;
import com.knowledge.assistant.repository.ChatHistoryRepository;
import com.knowledge.assistant.repository.UploadedFileRepository;
import com.knowledge.assistant.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stats")
public class UserStatsController {

    private final UserRepository userRepository;
    private final UploadedFileRepository fileRepository;
    private final ChatHistoryRepository chatRepository;

    public UserStatsController(
            UserRepository userRepository,
            UploadedFileRepository fileRepository,
            ChatHistoryRepository chatRepository
    ) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.chatRepository = chatRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserStats(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UploadedFile> files = fileRepository.findByUploadedById(user.getId());
        
        long totalUploads = files.size();
        long audioProcessed = files.stream().filter(f -> f.getFileType() != null && f.getFileType().startsWith("audio/")).count();
        long videoProcessed = files.stream().filter(f -> f.getFileType() != null && f.getFileType().startsWith("video/")).count();
        long totalChats = chatRepository.countByUserId(user.getId());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUploads", totalUploads);
        stats.put("totalChats", totalChats);
        stats.put("audioProcessed", audioProcessed);
        stats.put("videoProcessed", videoProcessed);

        return ResponseEntity.ok(stats);
    }
}
