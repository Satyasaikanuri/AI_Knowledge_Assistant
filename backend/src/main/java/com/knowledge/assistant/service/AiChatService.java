package com.knowledge.assistant.service;

import com.knowledge.assistant.dto.ChatRequest;
import com.knowledge.assistant.dto.ChatResponse;
import com.knowledge.assistant.entity.ChatHistory;
import com.knowledge.assistant.entity.TimestampReference;
import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.entity.User;
import com.knowledge.assistant.repository.ChatHistoryRepository;
import com.knowledge.assistant.repository.TimestampReferenceRepository;
import com.knowledge.assistant.repository.UploadedFileRepository;
import com.knowledge.assistant.repository.UserRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiChatService {

    private final VectorSearchService vectorSearchService;
    private final ChatHistoryRepository chatHistoryRepository;
    private final UploadedFileRepository fileRepository;
    private final UserRepository userRepository;
    private final TimestampReferenceRepository timestampRepository;
    private final ChatLanguageModel chatLanguageModel;

    public AiChatService(
            VectorSearchService vectorSearchService,
            ChatHistoryRepository chatHistoryRepository,
            UploadedFileRepository fileRepository,
            UserRepository userRepository,
            TimestampReferenceRepository timestampRepository,
            @Value("${groq.api-key}") String aiApiKey,
            @Value("${groq.base-url:https://api.groq.com/openai/v1}") String baseUrl,
            @Value("${groq.model-name:llama3-8b-8192}") String modelName
    ) {
        this.vectorSearchService = vectorSearchService;
        this.chatHistoryRepository = chatHistoryRepository;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.timestampRepository = timestampRepository;
        
        this.chatLanguageModel = OpenAiChatModel.builder()
                .apiKey(aiApiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.3)
                .build();
    }

    public ChatResponse askQuestion(ChatRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String question = request.getQuestion().replaceAll("^\"|\"$", "");
        log.info("Processing question: '{}' for fileId: {} and user: {}", question, request.getFileId(), userEmail);

        UploadedFile file = fileRepository.findById(request.getFileId())
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + request.getFileId()));

        // 1. Get relevant context from Vector DB (Increase context to 8 chunks)
        List<String> contextList = vectorSearchService.searchRelevantContext(question, request.getFileId(), 8);
        String context = String.join("\n\n---\n\n", contextList);

        // 2. Format Prompt (Improved for better summarization)
        String prompt = String.format(
                "You are an expert Document Assistant. Your task is to provide accurate information based ONLY on the provided context.\n\n" +
                "GUIDELINES:\n" +
                "1. If the information is in the context, provide a detailed and helpful answer.\n" +
                "2. If the user asks for a summary, synthesize the relevant parts of the context.\n" +
                "3. If the answer is absolutely not in the context, say: \"Information not found in uploaded document.\"\n" +
                "4. Always cite your sources using the [Source: Chunk X] markers provided.\n\n" +
                "DOCUMENT NAME: %s\n\n" +
                "DOCUMENT CONTEXT:\n%s\n\n" +
                "USER QUESTION:\n%s", 
                file.getOriginalFileName(), context, question
        );

        // 3. Get Answer from LLM
        String answer = chatLanguageModel.generate(prompt);

        // 4. Save Chat History
        ChatHistory history = ChatHistory.builder()
                .question(request.getQuestion())
                .answer(answer)
                .uploadedFile(file)
                .user(user)
                .build();
        chatHistoryRepository.save(history);

        // 5. Find matching timestamps if it's an audio/video file
        List<ChatResponse.TimestampDto> timestamps = null;
        if (file.getFileType() != null && (file.getFileType().startsWith("audio/") || file.getFileType().startsWith("video/"))) {
            // Find relevant timestamps by searching topics related to the question
            List<TimestampReference> refs = timestampRepository.searchByTopicAndFileId(request.getQuestion(), file.getId());
            timestamps = refs.stream()
                    .map(r -> new ChatResponse.TimestampDto(r.getStartTime(), r.getEndTime(), r.getTopic()))
                    .collect(Collectors.toList());
        }

        return ChatResponse.builder()
                .answer(answer)
                .sources(contextList)
                .timestamps(timestamps)
                .build();
    }
}
