package com.knowledge.assistant.dto;


public class ChatRequest {
    private String question;
    private Long fileId;

    public ChatRequest() {}

    public ChatRequest(String question, Long fileId) {
        this.question = question;
        this.fileId = fileId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
}
