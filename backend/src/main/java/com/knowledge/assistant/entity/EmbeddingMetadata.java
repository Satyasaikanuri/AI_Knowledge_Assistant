package com.knowledge.assistant.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
@Entity
@Table(name = "embedding_metadata")
public class EmbeddingMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vectorId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String chunkText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UploadedFile uploadedFile;

    public EmbeddingMetadata() {}

    public EmbeddingMetadata(Long id, String vectorId, String chunkText, UploadedFile uploadedFile) {
        this.id = id;
        this.vectorId = vectorId;
        this.chunkText = chunkText;
        this.uploadedFile = uploadedFile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVectorId() {
        return vectorId;
    }

    public void setVectorId(String vectorId) {
        this.vectorId = vectorId;
    }

    public String getChunkText() {
        return chunkText;
    }

    public void setChunkText(String chunkText) {
        this.chunkText = chunkText;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public static EmbeddingMetadataBuilder builder() {
        return new EmbeddingMetadataBuilder();
    }

    public static class EmbeddingMetadataBuilder {
        private Long id;
        private String vectorId;
        private String chunkText;
        private UploadedFile uploadedFile;

        public EmbeddingMetadataBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public EmbeddingMetadataBuilder vectorId(String vectorId) {
            this.vectorId = vectorId;
            return this;
        }

        public EmbeddingMetadataBuilder chunkText(String chunkText) {
            this.chunkText = chunkText;
            return this;
        }

        public EmbeddingMetadataBuilder uploadedFile(UploadedFile uploadedFile) {
            this.uploadedFile = uploadedFile;
            return this;
        }

        public EmbeddingMetadata build() {
            return new EmbeddingMetadata(id, vectorId, chunkText, uploadedFile);
        }
    }
}
