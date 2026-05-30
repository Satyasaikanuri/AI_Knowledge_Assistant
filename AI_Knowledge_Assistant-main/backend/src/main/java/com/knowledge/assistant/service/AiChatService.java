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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
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
        System.out.println("Processing question: '" + question + "' for fileId: " + request.getFileId() + " and user: " + userEmail);

        UploadedFile file = fileRepository.findById(request.getFileId())
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + request.getFileId()));

        // 1. Get relevant context from Vector DB (Increase context to 3 chunks to save memory)
        List<String> contextList = vectorSearchService.searchRelevantContext(question, request.getFileId(), 3);
        String context = String.join("\n\n---\n\n", contextList);

        // 2. Format Prompt (Improved for better summarization and timestamp citation)
        String prompt = String.format(
                "You are an expert Neural Knowledge Terminal. Your task is to provide accurate information based ONLY on the provided context.\n\n" +
                "CONTEXT FORMAT: [start_time - end_time]: Text content\n\n" +
                "GUIDELINES:\n" +
                "1. If the information is in the context, provide a detailed and helpful answer.\n" +
                "2. If the user asks for a summary, synthesize the relevant parts of the context.\n" +
                "3. IMPORTANT: When discussing content from audio/video files, ALWAYS cite the timestamp where the information occurs using the exact [start - end] format found in the context (e.g., \"The speaker mentions X at [12.50 - 15.20]\").\n" +
                "4. If the answer is not in the context, say: \"Information not found in uploaded neural units.\"\n" +
                "5. Always cite your chunk sources using the [Source: Chunk X] markers.\n\n" +
                "DOCUMENT NAME: %s\n\n" +
                "DOCUMENT CONTEXT:\n%s\n\n" +
                "USER QUESTION:\n%s", 
                file.getOriginalFileName(), context, question
        );

        // 3. Get Answer from LLM
        String answer = chatLanguageModel.generate(prompt);

        // 4. Save Chat History
        ChatHistory history = new ChatHistory();
        history.setQuestion(request.getQuestion());
        history.setAnswer(answer);
        history.setUploadedFile(file);
        history.setUser(user);
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

        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setAnswer(answer);
        chatResponse.setSources(contextList);
        chatResponse.setTimestamps(timestamps);
        return chatResponse;
    }
}
