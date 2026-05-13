package com.knowledge.assistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.assistant.entity.TimestampReference;
import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.repository.TimestampReferenceRepository;
import com.knowledge.assistant.repository.UploadedFileRepository;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Iterator;

@Service
public class WhisperTranscriptionService {

    @Value("${groq.api-key}")
    private String groqApiKey;

    private final UploadedFileRepository fileRepository;
    private final TimestampReferenceRepository timestampRepository;
    private final EmbeddingService embeddingService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WhisperTranscriptionService(
            UploadedFileRepository fileRepository,
            TimestampReferenceRepository timestampRepository,
            EmbeddingService embeddingService
    ) {
        this.fileRepository = fileRepository;
        this.timestampRepository = timestampRepository;
        this.embeddingService = embeddingService;
    }

    @Async
    public void processAudioVideoAsync(Long fileId) {
        System.out.println("Processing media file ID: " + fileId);
        try {
            UploadedFile uploadedFile = fileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File not found: " + fileId));

            File file = new File(uploadedFile.getFilePath());
            if (!file.exists()) {
                System.err.println("Media file not found on disk: " + uploadedFile.getFilePath());
                return;
            }

            // Groq has a 25MB limit
            long fileSizeInMb = file.length() / (1024 * 1024);
            
            if (fileSizeInMb > 25) {
                System.err.println("ERROR: File too large for Groq Whisper (Max 25MB). Current: " + fileSizeInMb + "MB");
                uploadedFile.setExtractedText("ERROR: File too large for transcription (Max 25MB). Please upload a smaller clip or MP3.");
                fileRepository.save(uploadedFile);
                return;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(groqApiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));
            body.add("model", "whisper-large-v3");
            body.add("response_format", "verbose_json");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String url = "https://api.groq.com/openai/v1/audio/transcriptions";
            
            System.out.println("Sending transcription request to Groq...");
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                String fullText = rootNode.path("text").asText();
                
                if (fullText == null || fullText.trim().isEmpty()) {
                    uploadedFile.setExtractedText("WARNING: No speech detected.");
                } else {
                    uploadedFile.setExtractedText(fullText);
                }
                fileRepository.save(uploadedFile);

                System.out.println("Transcription successful. Indexing to Pinecone...");
                JsonNode segmentsNode = rootNode.path("segments");
                StringBuilder documentContent = new StringBuilder();

                if (segmentsNode.isArray()) {
                    Iterator<JsonNode> elements = segmentsNode.elements();
                    while (elements.hasNext()) {
                        JsonNode segment = elements.next();
                        double start = segment.path("start").asDouble();
                        double end = segment.path("end").asDouble();
                        String text = segment.path("text").asText();

                        TimestampReference ref = new TimestampReference();
                        ref.setStartTime(start);
                        ref.setEndTime(end);
                        ref.setTranscriptText(text);
                        ref.setUploadedFile(uploadedFile);
                        ref.setTopic(generateTopicForSegment(text));
                        
                        timestampRepository.save(ref);
                        documentContent.append(String.format("[%.2f - %.2f]: %s\n", start, end, text));
                    }
                }

                // Chunk and embed
                Document document = Document.from(documentContent.toString(), new Metadata());
                embeddingService.chunkAndEmbedDocument(document, uploadedFile);

                System.out.println("Processing complete for file: " + uploadedFile.getOriginalFileName());
            } else {
                System.err.println("Groq API failed. Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Critical error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String generateTopicForSegment(String text) {
        // Simple fallback. Can be enhanced with an LLM call to extract main topic.
        if (text.length() > 50) {
            return text.substring(0, 50) + "...";
        }
        return text;
    }
}
