package com.knowledge.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    private Long id;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadTime;
    private String status;
}
