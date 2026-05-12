package com.knowledge.assistant.controller;

import com.knowledge.assistant.dto.ChatRequest;
import com.knowledge.assistant.dto.ChatResponse;
import com.knowledge.assistant.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AiChatService aiChatService;

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> askQuestion(
            @RequestBody ChatRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(aiChatService.askQuestion(request, authentication.getName()));
    }
}
