package com.knowledge.assistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.assistant.dto.ChatRequest;
import com.knowledge.assistant.dto.ChatResponse;
import com.knowledge.assistant.service.AiChatService;
import com.knowledge.assistant.config.RateLimitingService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiChatService aiChatService;

    @MockBean
    private RateLimitingService rateLimitingService;

    @MockBean
    private Bucket bucket;

    @BeforeEach
    void setUp() {
        when(rateLimitingService.resolveChatBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);
    }

    @Test
    @WithMockUser
    void askQuestion_Success() throws Exception {
        ChatRequest request = new ChatRequest();
        request.setQuestion("Test Question");
        request.setFileId(1L);

        ChatResponse response = new ChatResponse();
        response.setAnswer("Test Answer");

        when(aiChatService.askQuestion(anyString(), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/v1/chat/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("Test Answer"));
    }

    @Test
    @WithMockUser
    void askQuestion_RateLimited() throws Exception {
        when(bucket.tryConsume(1)).thenReturn(false);

        ChatRequest request = new ChatRequest();
        request.setQuestion("Test Question");

        mockMvc.perform(post("/api/v1/chat/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests());
    }
}
