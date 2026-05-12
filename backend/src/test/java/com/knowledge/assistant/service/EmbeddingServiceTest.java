package com.knowledge.assistant.service;

import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.repository.EmbeddingMetadataRepository;
import com.knowledge.assistant.repository.UploadedFileRepository;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmbeddingServiceTest {

    @Mock
    private EmbeddingMetadataRepository metadataRepository;
    @Mock
    private UploadedFileRepository fileRepository;
    
    private EmbeddingService embeddingService;

    @BeforeEach
    void setUp() {
        // Since the constructor initializes Pinecone which requires API keys,
        // we might need a modified version for unit tests or use reflection to inject mocks.
        // For this task, we will mock the critical parts.
        embeddingService = new EmbeddingService(metadataRepository, fileRepository, "test-key", "env", "project", "index");
    }

    @Test
    void deleteEmbeddingsByFileId_Success() {
        Long fileId = 1L;
        when(metadataRepository.findByUploadedFileId(fileId)).thenReturn(Collections.emptyList());

        embeddingService.deleteEmbeddingsByFileId(fileId);

        verify(metadataRepository, times(1)).findByUploadedFileId(fileId);
        verify(metadataRepository, times(1)).deleteAll(anyList());
    }
}
