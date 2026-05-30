package com.knowledge.assistant.service;

import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.repository.UploadedFileRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SummaryService {

    private final UploadedFileRepository fileRepository;
    private final ChatLanguageModel chatLanguageModel;

    public SummaryService(
            UploadedFileRepository fileRepository,
            @Value("${groq.api-key}") String groqApiKey,
            @Value("${groq.base-url}") String groqBaseUrl,
            @Value("${groq.model-name}") String groqModelName
    ) {
        this.fileRepository = fileRepository;
        this.chatLanguageModel = OpenAiChatModel.builder()
                .baseUrl(groqBaseUrl)
                .apiKey(groqApiKey)
                .modelName(groqModelName)
                .temperature(0.3)
                .build();
    }

    @Cacheable(value = "summaries", key = "#fileId")
    public String generateSummary(Long fileId) {
        System.out.println("Generating summary for file ID: " + fileId);
        
        UploadedFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (file.getSummary() != null && !file.getSummary().isEmpty()) {
            return file.getSummary();
        }

        String textToSummarize = file.getExtractedText();
        
        if (textToSummarize == null || textToSummarize.isEmpty()) {
            throw new RuntimeException("No extracted text found to summarize.");
        }

        // Truncate if too long for the model context
        if (textToSummarize.length() > 50000) {
            textToSummarize = textToSummarize.substring(0, 50000) + "...";
        }

        String prompt = "Provide a comprehensive and well-structured summary of the following text:\n\n" + textToSummarize;
        
        String summary = chatLanguageModel.generate(prompt);
        
        file.setSummary(summary);
        fileRepository.save(file);
        
        return summary;
    }
}
