package com.knowledge.assistant.service;

import com.knowledge.assistant.dto.UploadResponse;
import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.entity.User;
import com.knowledge.assistant.repository.UploadedFileRepository;
import com.knowledge.assistant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;
    private UploadedFileRepository fileRepository;
    private UserRepository userRepository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileRepository = mock(UploadedFileRepository.class);
        userRepository = mock(UserRepository.class);
        fileStorageService = new FileStorageService(fileRepository, userRepository);
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
    }

    @Test
    void storeFile_Success() throws IOException {
        String userEmail = "test@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(userEmail);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", 
                new byte[]{0x25, 0x50, 0x44, 0x46, 0x00, 0x00, 0x00, 0x00} // PDF Magic bytes
        );

        UploadedFile savedFile = new UploadedFile();
        savedFile.setId(10L);
        savedFile.setOriginalFileName("test.pdf");
        savedFile.setFilePath(tempDir.resolve("uuid.pdf").toString());

        when(fileRepository.save(any(UploadedFile.class))).thenReturn(savedFile);

        UploadResponse response = fileStorageService.storeFile(file, userEmail);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        verify(fileRepository, times(1)).save(any(UploadedFile.class));
    }

    @Test
    void storeFile_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test".getBytes());

        assertThrows(RuntimeException.class, () -> fileStorageService.storeFile(file, "unknown@test.com"));
    }
}
