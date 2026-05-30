package com.knowledge.assistant.service;

import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.repository.TimestampReferenceRepository;
import com.knowledge.assistant.repository.UploadedFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WhisperTranscriptionServiceTest {

    @Mock
    private UploadedFileRepository fileRepository;
    @Mock
    private TimestampReferenceRepository timestampRepository;
    @Mock
    private EmbeddingService embeddingService;

    @InjectMocks
    private WhisperTranscriptionService whisperTranscriptionService;

    @Test
    void processAudioVideoAsync_FileNotFound() {
        when(fileRepository.findById(anyLong())).thenReturn(Optional.empty());

        whisperTranscriptionService.processAudioVideoAsync(1L);

        // Should log error but not crash
        verify(fileRepository, times(1)).findById(1L);
        verifyNoInteractions(timestampRepository);
    }

    @Test
    void processAudioVideoAsync_FileNotOnDisk() {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFilePath("non_existent_path.mp3");
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(uploadedFile));

        whisperTranscriptionService.processAudioVideoAsync(1L);

        // Should catch exception and log it
        verify(fileRepository, times(1)).findById(1L);
        verifyNoInteractions(embeddingService);
    }
}
