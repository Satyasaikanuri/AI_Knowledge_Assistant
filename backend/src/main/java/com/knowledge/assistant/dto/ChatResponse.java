package com.knowledge.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String answer;
    private List<String> sources;
    private List<TimestampDto> timestamps;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimestampDto {
        private Double startTime;
        private Double endTime;
        private String topic;
    }
}
