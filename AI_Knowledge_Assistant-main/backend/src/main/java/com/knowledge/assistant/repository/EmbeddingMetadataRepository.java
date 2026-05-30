package com.knowledge.assistant.repository;

import com.knowledge.assistant.entity.EmbeddingMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmbeddingMetadataRepository extends JpaRepository<EmbeddingMetadata, Long> {
    List<EmbeddingMetadata> findByUploadedFileId(Long fileId);
}
