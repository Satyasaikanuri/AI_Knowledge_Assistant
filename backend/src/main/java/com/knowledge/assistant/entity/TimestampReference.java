package com.knowledge.assistant.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
@Entity
@Table(name = "timestamp_references")
public class TimestampReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Column(nullable = false)
    private Double startTime;

    @Column(nullable = false)
    private Double endTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String transcriptText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UploadedFile uploadedFile;

    public TimestampReference() {}

    public TimestampReference(Long id, String topic, Double startTime, Double endTime, String transcriptText, UploadedFile uploadedFile) {
        this.id = id;
        this.topic = topic;
        this.startTime = startTime;
        this.endTime = endTime;
        this.transcriptText = transcriptText;
        this.uploadedFile = uploadedFile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Double getStartTime() {
        return startTime;
    }

    public void setStartTime(Double startTime) {
        this.startTime = startTime;
    }

    public Double getEndTime() {
        return endTime;
    }

    public void setEndTime(Double endTime) {
        this.endTime = endTime;
    }

    public String getTranscriptText() {
        return transcriptText;
    }

    public void setTranscriptText(String transcriptText) {
        this.transcriptText = transcriptText;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public static TimestampReferenceBuilder builder() {
        return new TimestampReferenceBuilder();
    }

    public static class TimestampReferenceBuilder {
        private Long id;
        private String topic;
        private Double startTime;
        private Double endTime;
        private String transcriptText;
        private UploadedFile uploadedFile;

        public TimestampReferenceBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TimestampReferenceBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public TimestampReferenceBuilder startTime(Double startTime) {
            this.startTime = startTime;
            return this;
        }

        public TimestampReferenceBuilder endTime(Double endTime) {
            this.endTime = endTime;
            return this;
        }

        public TimestampReferenceBuilder transcriptText(String transcriptText) {
            this.transcriptText = transcriptText;
            return this;
        }

        public TimestampReferenceBuilder uploadedFile(UploadedFile uploadedFile) {
            this.uploadedFile = uploadedFile;
            return this;
        }

        public TimestampReference build() {
            return new TimestampReference(id, topic, startTime, endTime, transcriptText, uploadedFile);
        }
    }
}
