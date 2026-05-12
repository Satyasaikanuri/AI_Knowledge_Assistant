package com.knowledge.assistant.service;

import com.knowledge.assistant.dto.ChatResponse;
import com.knowledge.assistant.entity.TimestampReference;
import com.knowledge.assistant.repository.ChatHistoryRepository;
import com.knowledge.assistant.repository.TimestampReferenceRepository;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {

    @Mock
    private ChatHistoryRepository chatHistoryRepository;
    @Mock
    private TimestampReferenceRepository timestampRepository;
    @Mock
    private VectorSearchService vectorSearchService;
    @Mock
    private OpenAiChatModel chatModel;

    @InjectMocks
    private AiChatService aiChatService;

    @BeforeEach
    void setUp() {
        // chatModel is initialized in the constructor with @Value, 
        // so we might need to manually set it or use reflection if it's not a mockable bean easily.
        // For unit testing, we usually mock the behavior of the model.
    }

    @Test
    void askQuestion_Success() {
        String question = "What is RAG?";
        Long fileId = 1L;
        List<String> context = List.of("RAG stands for Retrieval-Augmented Generation.");
        
        when(vectorSearchService.searchRelevantContext(anyString(), anyLong(), anyInt())).thenReturn(context);
        when(chatModel.generate(anyString())).thenReturn("AI Answer");
        when(timestampRepository.findByUploadedFileId(anyLong())).thenReturn(Collections.emptyList());

        ChatResponse response = aiChatService.askQuestion(question, fileId);

        assertNotNull(response);
        assertEquals("AI Answer", response.getAnswer());
        verify(chatHistoryRepository, times(1)).save(any());
    }

    @Test
    void askQuestion_NoContext() {
        String question = "Unknown?";
        Long fileId = 1L;
        
        when(vectorSearchService.searchRelevantContext(anyString(), anyLong(), anyInt())).thenReturn(Collections.emptyList());
        when(chatModel.generate(anyString())).thenReturn("I don't know.");

        ChatResponse response = aiChatService.askQuestion(question, fileId);

        assertNotNull(response);
        assertTrue(response.getAnswer().contains("I don't know"));
    }
}
