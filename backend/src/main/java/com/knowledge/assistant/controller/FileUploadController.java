package com.knowledge.assistant.controller;

import com.knowledge.assistant.dto.UploadResponse;
import com.knowledge.assistant.service.EmbeddingService;
import com.knowledge.assistant.service.FileStorageService;
import com.knowledge.assistant.service.PdfProcessingService;
import com.knowledge.assistant.service.WhisperTranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final PdfProcessingService pdfProcessingService;
    private final WhisperTranscriptionService whisperTranscriptionService;
    private final EmbeddingService embeddingService;
    private final com.knowledge.assistant.repository.ChatHistoryRepository chatHistoryRepository;
    private final com.knowledge.assistant.repository.TimestampReferenceRepository timestampReferenceRepository;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        try {
            UploadResponse response = fileStorageService.storeFile(file, authentication.getName());
            
            // Trigger async processing
            if ("application/pdf".equals(response.getFileType())) {
                pdfProcessingService.processPdfAsync(response.getId());
            } else if (response.getFileType().startsWith("audio/") || response.getFileType().startsWith("video/")) {
                whisperTranscriptionService.processAudioVideoAsync(response.getId());
            }

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file. Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteFile(@PathVariable Long id, Authentication authentication) {
        com.knowledge.assistant.entity.UploadedFile fileEntity = fileStorageService.getFileEntity(id);
        if (!fileEntity.getUploadedBy().getEmail().equals(authentication.getName())) {
            return ResponseEntity.status(403).build();
        }
        
        // Manual Cascade cleanup to prevent FK constraint errors
        chatHistoryRepository.deleteByUploadedFileId(id);
        timestampReferenceRepository.deleteByUploadedFileId(id);
        
        // 1. Delete embeddings from Pinecone
        embeddingService.deleteEmbeddingsByFileId(id);
        
        // 2. Delete physical file and DB record
        fileStorageService.deleteFile(id);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<UploadResponse>> listFiles(Authentication authentication) {
        return ResponseEntity.ok(fileStorageService.getUserFiles(authentication.getName()));
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<org.springframework.core.io.Resource> streamFile(@PathVariable Long id, Authentication authentication) {
        com.knowledge.assistant.entity.UploadedFile fileEntity = fileStorageService.getFileEntity(id);
        if (!fileEntity.getUploadedBy().getEmail().equals(authentication.getName())) {
            return ResponseEntity.status(403).build();
        }
        java.io.File file = fileStorageService.getFile(id);
        org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(file);
        
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(fileEntity.getFileType()))
                .body(resource);
    }
}
