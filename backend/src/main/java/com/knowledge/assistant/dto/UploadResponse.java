package com.knowledge.assistant.dto;

import java.time.LocalDateTime;

public class UploadResponse {
    private Long id;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadTime;
    private String status;

    public UploadResponse() {}

    public UploadResponse(Long id, String originalFileName, String fileType, Long fileSize, LocalDateTime uploadTime, String status) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadTime = uploadTime;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static UploadResponseBuilder builder() {
        return new UploadResponseBuilder();
    }

    public static class UploadResponseBuilder {
        private Long id;
        private String originalFileName;
        private String fileType;
        private Long fileSize;
        private LocalDateTime uploadTime;
        private String status;

        public UploadResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UploadResponseBuilder originalFileName(String originalFileName) {
            this.originalFileName = originalFileName;
            return this;
        }

        public UploadResponseBuilder fileType(String fileType) {
            this.fileType = fileType;
            return this;
        }

        public UploadResponseBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public UploadResponseBuilder uploadTime(LocalDateTime uploadTime) {
            this.uploadTime = uploadTime;
            return this;
        }

        public UploadResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public UploadResponse build() {
            return new UploadResponse(id, originalFileName, fileType, fileSize, uploadTime, status);
        }
    }
}
