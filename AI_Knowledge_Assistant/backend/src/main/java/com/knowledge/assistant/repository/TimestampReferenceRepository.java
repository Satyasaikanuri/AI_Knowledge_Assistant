package com.knowledge.assistant.repository;

import com.knowledge.assistant.entity.TimestampReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimestampReferenceRepository extends JpaRepository<TimestampReference, Long> {
    
    @Query("SELECT t FROM TimestampReference t WHERE t.uploadedFile.id = :fileId AND LOWER(t.topic) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TimestampReference> searchByTopicAndFileId(@Param("query") String query, @Param("fileId") Long fileId);

    List<TimestampReference> findByUploadedFileIdOrderByStartTimeAsc(Long fileId);

    void deleteByUploadedFileId(Long fileId);
}
