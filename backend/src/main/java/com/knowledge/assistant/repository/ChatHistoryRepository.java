package com.knowledge.assistant.repository;

import com.knowledge.assistant.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUserIdAndUploadedFileIdOrderByCreatedAtAsc(Long userId, Long fileId);
    long countByUserId(Long userId);
    void deleteByUploadedFileId(Long fileId);
}
