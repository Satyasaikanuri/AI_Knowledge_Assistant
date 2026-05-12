package com.knowledge.assistant.controller;

import com.knowledge.assistant.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/{fileId}")
    public ResponseEntity<String> getSummary(@PathVariable Long fileId) {
        return ResponseEntity.ok(summaryService.generateSummary(fileId));
    }
}
