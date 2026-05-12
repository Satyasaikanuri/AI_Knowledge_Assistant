package com.knowledge.assistant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "uploaded_files", indexes = {
    @Index(name = "idx_uploaded_by", columnList = "user_id"),
    @Index(name = "idx_file_type", columnList = "fileType")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
