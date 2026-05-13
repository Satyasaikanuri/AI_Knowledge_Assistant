package com.knowledge.assistant.controller;

import com.knowledge.assistant.dto.ChatRequest;
import com.knowledge.assistant.dto.ChatResponse;
import com.knowledge.assistant.service.AiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final AiChatService aiChatService;

    public ChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> askQuestion(
            @RequestBody ChatRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(aiChatService.askQuestion(request, authentication.getName()));
    }
}
