package com.knowledge.assistant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "timestamp_references")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
