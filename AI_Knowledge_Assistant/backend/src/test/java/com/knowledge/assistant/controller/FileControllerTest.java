package com.knowledge.assistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.assistant.service.FileStorageService;
import com.knowledge.assistant.service.WhisperTranscriptionService;
import com.knowledge.assistant.service.PdfProcessingService;
import com.knowledge.assistant.service.EmbeddingService;
import com.knowledge.assistant.repository.UploadedFileRepository;
import com.knowledge.assistant.config.RateLimitingService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateLimitingService rateLimitingService;

    @MockBean
    private Bucket bucket;

    @BeforeEach
    void setUp() {
        when(rateLimitingService.resolveGeneralBucket(anyString())).thenReturn(bucket);
        when(rateLimitingService.resolveUploadBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);
    }

    @Test
    @WithMockUser
    void listFiles_Success() throws Exception {
        mockMvc.perform(get("/api/v1/files/list"))
                .andExpect(status().isOk());
    }

    @Test
    void listFiles_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/files/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void uploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "PDF CONTENT".getBytes());
        
        mockMvc.perform(multipart("/api/v1/files/upload").file(file))
                .andExpect(status().isOk());
    }
}
