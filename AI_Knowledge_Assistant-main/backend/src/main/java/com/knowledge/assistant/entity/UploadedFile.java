package com.knowledge.assistant.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "uploaded_files", indexes = {
    @Index(name = "idx_uploaded_by", columnList = "user_id"),
    @Index(name = "idx_file_type", columnList = "fileType")
})
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadTime;

    @Column(nullable = false)
    private String filePath;

    @Column(columnDefinition = "LONGTEXT")
    private String extractedText;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User uploadedBy;

    public UploadedFile() {}

    public UploadedFile(Long id, String originalFileName, String storedFileName, String fileType, Long fileSize, LocalDateTime uploadTime, String filePath, String extractedText, String summary, User uploadedBy) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadTime = uploadTime;
        this.filePath = filePath;
        this.extractedText = extractedText;
        this.summary = summary;
        this.uploadedBy = uploadedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public static UploadedFileBuilder builder() {
        return new UploadedFileBuilder();
    }

    public static class UploadedFileBuilder {
        private Long id;
        private String originalFileName;
        private String storedFileName;
        private String fileType;
        private Long fileSize;
        private LocalDateTime uploadTime;
        private String filePath;
        private String extractedText;
        private String summary;
        private User uploadedBy;

        public UploadedFileBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UploadedFileBuilder originalFileName(String originalFileName) {
            this.originalFileName = originalFileName;
            return this;
        }

        public UploadedFileBuilder storedFileName(String storedFileName) {
            this.storedFileName = storedFileName;
            return this;
        }

        public UploadedFileBuilder fileType(String fileType) {
            this.fileType = fileType;
            return this;
        }

        public UploadedFileBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public UploadedFileBuilder uploadTime(LocalDateTime uploadTime) {
            this.uploadTime = uploadTime;
            return this;
        }

        public UploadedFileBuilder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public UploadedFileBuilder extractedText(String extractedText) {
            this.extractedText = extractedText;
            return this;
        }

        public UploadedFileBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public UploadedFileBuilder uploadedBy(User uploadedBy) {
            this.uploadedBy = uploadedBy;
            return this;
        }

        public UploadedFile build() {
            return new UploadedFile(id, originalFileName, storedFileName, fileType, fileSize, uploadTime, filePath, extractedText, summary, uploadedBy);
        }
    }
}
