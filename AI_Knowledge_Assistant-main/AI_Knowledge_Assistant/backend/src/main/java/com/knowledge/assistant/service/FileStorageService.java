package com.knowledge.assistant.service;

import com.knowledge.assistant.dto.UploadResponse;
import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.entity.User;
import com.knowledge.assistant.repository.UploadedFileRepository;
import com.knowledge.assistant.repository.UserRepository;
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
public class FileStorageService {

    @Value("${app.storage.upload-dir:uploads}")
    private String uploadDir;

    private final UploadedFileRepository fileRepository;
    private final UserRepository userRepository;

    public FileStorageService(UploadedFileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

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

        System.out.println("Attempting to store file: " + originalFilename + " for user: " + userEmail);

        try {
            Path targetLocation = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setOriginalFileName(originalFilename);
            uploadedFile.setStoredFileName(storedFileName);
            uploadedFile.setFileType(contentType);
            uploadedFile.setFileSize(file.getSize());
            uploadedFile.setFilePath(targetLocation.toAbsolutePath().toString());
            uploadedFile.setUploadedBy(user);

            UploadedFile savedFile = fileRepository.save(uploadedFile);
            System.out.println("File successfully ingested and indexed: " + savedFile.getOriginalFileName());

            UploadResponse response = new UploadResponse();
            response.setId(savedFile.getId());
            response.setOriginalFileName(savedFile.getOriginalFileName());
            response.setFileType(savedFile.getFileType());
            response.setFileSize(savedFile.getFileSize());
            response.setUploadTime(savedFile.getUploadTime());
            response.setStatus("UPLOADED");
            return response;
        } catch (Exception e) {
            System.out.println("INGESTION CRITICAL FAILURE for file " + originalFilename + ": " + e.getMessage());
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
                .map(f -> {
                    UploadResponse res = new UploadResponse();
                    res.setId(f.getId());
                    res.setOriginalFileName(f.getOriginalFileName());
                    res.setFileType(f.getFileType());
                    res.setFileSize(f.getFileSize());
                    res.setUploadTime(f.getUploadTime());
                    res.setStatus("UPLOADED");
                    return res;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public void deleteFile(Long id) {
        UploadedFile uploadedFile = getFileEntity(id);
        
        // Delete physical file
        try {
            Path filePath = Paths.get(uploadedFile.getFilePath());
            Files.deleteIfExists(filePath);
            System.out.println("Physical file deleted: " + uploadedFile.getOriginalFileName());
        } catch (IOException e) {
            System.out.println("ERROR: Failed to delete physical file: " + e.getMessage());
            // Continue to delete from DB even if disk deletion fails
        }

        // Delete from database
        fileRepository.delete(uploadedFile);
        System.out.println("File record deleted from database: " + id);
    }
}
