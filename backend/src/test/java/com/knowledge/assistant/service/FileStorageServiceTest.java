package com.knowledge.assistant.service;

import com.knowledge.assistant.config.FileStorageProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;
    private FileStorageProperties fileStorageProperties;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageProperties = new FileStorageProperties();
        fileStorageProperties.setUploadDir(tempDir.toString());
        fileStorageService = new FileStorageService(fileStorageProperties);
    }

    @Test
    void storeFile_Success() throws IOException {
        String fileName = "test.txt";
        MultipartFile multipartFile = new MockMultipartFile("file", fileName, "text/plain", "Hello World".getBytes());

        String storedName = fileStorageService.storeFile(multipartFile);

        assertNotNull(storedName);
        assertTrue(storedName.endsWith(".txt"));
        assertTrue(Files.exists(tempDir.resolve(storedName)));
    }

    @Test
    void storeFile_InvalidFileName() {
        MultipartFile multipartFile = new MockMultipartFile("file", "..invalid.txt", "text/plain", "test".getBytes());

        assertThrows(RuntimeException.class, () -> fileStorageService.storeFile(multipartFile));
    }

    @Test
    void deleteFile_Success() throws IOException {
        Path filePath = tempDir.resolve("to_delete.txt");
        Files.write(filePath, "test content".getBytes());

        fileStorageService.deleteFile("to_delete.txt");

        assertFalse(Files.exists(filePath));
    }

    @Test
    void deleteFile_NonExistent() {
        // Should not throw exception
        assertDoesNotThrow(() -> fileStorageService.deleteFile("non_existent.txt"));
    }
}
