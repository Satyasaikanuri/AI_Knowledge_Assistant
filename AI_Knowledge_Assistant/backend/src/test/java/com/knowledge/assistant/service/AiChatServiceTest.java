package com.knowledge.assistant.service;

import com.knowledge.assistant.dto.ChatRequest;
import com.knowledge.assistant.dto.ChatResponse;
import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.entity.User;
import com.knowledge.assistant.repository.ChatHistoryRepository;
import com.knowledge.assistant.repository.TimestampReferenceRepository;
import com.knowledge.assistant.repository.UploadedFileRepository;
import com.knowledge.assistant.repository.UserRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private UploadedFileRepository fileRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatLanguageModel chatLanguageModel;

    private AiChatService aiChatService;

    @BeforeEach
    void setUp() {
        // We create the service manually to inject the mocked chatLanguageModel
        aiChatService = new AiChatService(
                vectorSearchService,
                chatHistoryRepository,
                fileRepository,
                userRepository,
                timestampRepository,
                "dummy-key",
                "http://dummy",
                "dummy-model"
        );
        // Inject the mocked language model instead of the one built in constructor
        ReflectionTestUtils.setField(aiChatService, "chatLanguageModel", chatLanguageModel);
    }

    @Test
    void askQuestion_Success() {
        ChatRequest request = new ChatRequest();
        request.setQuestion("What is RAG?");
        request.setFileId(1L);
        
        String userEmail = "test@example.com";
        User user = new User();
        user.setEmail(userEmail);
        user.setId(10L);

        UploadedFile file = new UploadedFile();
        file.setId(1L);
        file.setOriginalFileName("test.pdf");
        file.setFileType("application/pdf");

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(vectorSearchService.searchRelevantContext(anyString(), anyLong(), anyInt())).thenReturn(List.of("RAG context"));
        when(chatLanguageModel.generate(anyString())).thenReturn("AI Answer");

        ChatResponse response = aiChatService.askQuestion(request, userEmail);

        assertNotNull(response);
        assertEquals("AI Answer", response.getAnswer());
        verify(chatHistoryRepository, times(1)).save(any());
    }
}
