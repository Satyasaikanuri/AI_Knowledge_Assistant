package com.knowledge.assistant.dto;

import java.util.List;

public class ChatResponse {
    private String answer;
    private List<String> sources;
    private List<TimestampDto> timestamps;

    public ChatResponse() {}

    public ChatResponse(String answer, List<String> sources, List<TimestampDto> timestamps) {
        this.answer = answer;
        this.sources = sources;
        this.timestamps = timestamps;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public List<TimestampDto> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<TimestampDto> timestamps) {
        this.timestamps = timestamps;
    }

    public static ChatResponseBuilder builder() {
        return new ChatResponseBuilder();
    }

    public static class ChatResponseBuilder {
        private String answer;
        private List<String> sources;
        private List<TimestampDto> timestamps;

        public ChatResponseBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public ChatResponseBuilder sources(List<String> sources) {
            this.sources = sources;
            return this;
        }

        public ChatResponseBuilder timestamps(List<TimestampDto> timestamps) {
            this.timestamps = timestamps;
            return this;
        }

        public ChatResponse build() {
            return new ChatResponse(answer, sources, timestamps);
        }
    }

    public static class TimestampDto {
        private Double startTime;
        private Double endTime;
        private String topic;

        public TimestampDto() {}

        public TimestampDto(Double startTime, Double endTime, String topic) {
            this.startTime = startTime;
            this.endTime = endTime;
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

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public static TimestampDtoBuilder builder() {
            return new TimestampDtoBuilder();
        }

        public static class TimestampDtoBuilder {
            private Double startTime;
            private Double endTime;
            private String topic;

            public TimestampDtoBuilder startTime(Double startTime) {
                this.startTime = startTime;
                return this;
            }

            public TimestampDtoBuilder endTime(Double endTime) {
                this.endTime = endTime;
                return this;
            }

            public TimestampDtoBuilder topic(String topic) {
                this.topic = topic;
                return this;
            }

            public TimestampDto build() {
                return new TimestampDto(startTime, endTime, topic);
            }
        }
    }
}
