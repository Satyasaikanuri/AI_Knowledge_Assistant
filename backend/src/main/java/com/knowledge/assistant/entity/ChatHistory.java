package com.knowledge.assistant.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history", indexes = {
    @Index(name = "idx_chat_user", columnList = "user_id"),
    @Index(name = "idx_chat_file", columnList = "file_id")
})
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UploadedFile uploadedFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ChatHistory() {}

    public ChatHistory(Long id, String question, String answer, LocalDateTime createdAt, UploadedFile uploadedFile, User user) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.createdAt = createdAt;
        this.uploadedFile = uploadedFile;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static ChatHistoryBuilder builder() {
        return new ChatHistoryBuilder();
    }

    public static class ChatHistoryBuilder {
        private Long id;
        private String question;
        private String answer;
        private LocalDateTime createdAt;
        private UploadedFile uploadedFile;
        private User user;

        public ChatHistoryBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ChatHistoryBuilder question(String question) {
            this.question = question;
            return this;
        }

        public ChatHistoryBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public ChatHistoryBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ChatHistoryBuilder uploadedFile(UploadedFile uploadedFile) {
            this.uploadedFile = uploadedFile;
            return this;
        }

        public ChatHistoryBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ChatHistory build() {
            return new ChatHistory(id, question, answer, createdAt, uploadedFile, user);
        }
    }
}
