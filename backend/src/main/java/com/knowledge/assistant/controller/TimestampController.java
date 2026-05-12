package com.knowledge.assistant.controller;

import com.knowledge.assistant.entity.TimestampReference;
import com.knowledge.assistant.repository.TimestampReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timestamps")
@RequiredArgsConstructor
public class TimestampController {

    private final TimestampReferenceRepository timestampRepository;

    @GetMapping("/{fileId}")
    public ResponseEntity<List<TimestampReference>> getTimestamps(
            @PathVariable Long fileId,
            @RequestParam(required = false) String query
    ) {
        if (query != null && !query.isEmpty()) {
            return ResponseEntity.ok(timestampRepository.searchByTopicAndFileId(query, fileId));
        }
        return ResponseEntity.ok(timestampRepository.findByUploadedFileIdOrderByStartTimeAsc(fileId));
    }
}
