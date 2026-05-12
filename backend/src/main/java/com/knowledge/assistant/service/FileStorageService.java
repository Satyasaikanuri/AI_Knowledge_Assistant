package com.knowledge.assistant.service;

import com.knowledge.assistant.dto.UploadResponse;
import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.entity.User;
import com.knowledge.assistant.repository.UploadedFileRepository;
import com.knowledge.assistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${app.storage.upload-dir:uploads}")
    private String uploadDir;

    private final UploadedFileRepository fileRepository;
    private final UserRepository userRepository;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "application/pdf", "audio/mpeg", "audio/wav", "video/mp4", "video/quicktime"
    );

    public UploadResponse storeFile(MultipartFile file, String userEmail) throws IOException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_EXTENSIONS.contains(contentType)) {
            throw new RuntimeException("Invalid file type. Only PDF, MP3, WAV, MP4, and MOV are allowed.");
        }
        
        // Magic Byte Validation
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            int bytesRead = is.read(header);
            if (bytesRead >= 4) {
                boolean isPdf = (header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46);
                boolean isRiff = (header[0] == 0x52 && header[1] == 0x49 && header[2] == 0x46 && header[3] == 0x46); // WAV/AVI
                boolean isId3 = (header[0] == 0x49 && header[1] == 0x44 && header[2] == 0x33); // MP3
                boolean isFtyp = (bytesRead >= 8 && header[4] == 0x66 && header[5] == 0x74 && header[6] == 0x79 && header[7] == 0x70); // MP4/MOV
                boolean isMp3Sync = (header[0] == (byte) 0xFF && (header[1] & 0xE0) == 0xE0); // MP3 no ID3

                if (!isPdf && !isRiff && !isId3 && !isFtyp && !isMp3Sync) {
                    throw new RuntimeException("File content does not match allowed types. Malware validation failed.");
                }
            } else {
                throw new RuntimeException("File is too small to validate.");
            }
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("Original filename cannot be null");
        }

        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }

        String storedFileName = UUID.randomUUID().toString() + extension;
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        log.info("Attempting to store file: {} for user: {}", originalFilename, userEmail);

        try {
            Path targetLocation = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            UploadedFile uploadedFile = UploadedFile.builder()
                    .originalFileName(originalFilename)
                    .storedFileName(storedFileName)
                    .fileType(contentType)
                    .fileSize(file.getSize())
                    .filePath(targetLocation.toAbsolutePath().toString())
                    .uploadedBy(user)
                    .build();

            UploadedFile savedFile = fileRepository.save(uploadedFile);
            log.info("File successfully ingested and indexed: {}", savedFile.getOriginalFileName());

            return UploadResponse.builder()
                    .id(savedFile.getId())
                    .originalFileName(savedFile.getOriginalFileName())
                    .fileType(savedFile.getFileType())
                    .fileSize(savedFile.getFileSize())
                    .uploadTime(savedFile.getUploadTime())
                    .status("UPLOADED")
                    .build();
        } catch (Exception e) {
            log.error("INGESTION CRITICAL FAILURE for file {}: {}", originalFilename, e.getMessage());
            throw new RuntimeException("Neural Ingestion Matrix Error: " + e.getMessage());
        }
    }
    
    public UploadedFile getFileEntity(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
    }

    public File getFile(Long id) {
        UploadedFile uploadedFile = getFileEntity(id);
        File file = new File(uploadedFile.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("File not found on disk");
        }
        return file;
    }

    public List<UploadResponse> getUserFiles(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return fileRepository.findByUploadedById(user.getId()).stream()
                .map(file -> UploadResponse.builder()
                        .id(file.getId())
                        .originalFileName(file.getOriginalFileName())
                        .fileType(file.getFileType())
                        .fileSize(file.getFileSize())
                        .uploadTime(file.getUploadTime())
                        .status("UPLOADED")
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    public void deleteFile(Long id) {
        UploadedFile uploadedFile = getFileEntity(id);
        
        // Delete physical file
        try {
            Path filePath = Paths.get(uploadedFile.getFilePath());
            Files.deleteIfExists(filePath);
            log.info("Physical file deleted: {}", uploadedFile.getOriginalFileName());
        } catch (IOException e) {
            log.error("Failed to delete physical file: {}", e.getMessage());
            // Continue to delete from DB even if disk deletion fails
        }

        // Delete from database
        fileRepository.delete(uploadedFile);
        log.info("File record deleted from database: {}", id);
    }
}
