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

    @Value("${groq.base-url}")
    private String groqBaseUrl;

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
        System.out.println("Starting Whisper transcription for file ID: " + fileId);
        try {
            UploadedFile uploadedFile = fileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File not found: " + fileId));

            File file = new File(uploadedFile.getFilePath());
            if (!file.exists()) {
                throw new RuntimeException("Media file not found on disk: " + uploadedFile.getFilePath());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(groqApiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));
            body.add("model", "whisper-large-v3");
            body.add("response_format", "verbose_json"); // We need verbose_json for timestamps

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String url = groqBaseUrl + "/audio/transcriptions";
            
            System.out.println("Sending request to Groq Whisper API...");
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                String fullText = rootNode.path("text").asText();
                
                uploadedFile.setExtractedText(fullText);
                fileRepository.save(uploadedFile);

                System.out.println("Successfully transcribed media. Extracting timestamps...");
                
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
                        
                        // Build text for embedding with timestamp context
                        documentContent.append(String.format("[%.2f - %.2f]: %s\n", start, end, text));
                    }
                }

                // Chunk and embed the transcription text
                Document document = Document.from(documentContent.toString(), new Metadata());
                embeddingService.chunkAndEmbedDocument(document, uploadedFile);

                System.out.println("Finished processing audio/video for file ID: " + fileId);
            } else {
                System.out.println("ERROR: Groq Whisper API call failed with status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.out.println("Error during Whisper transcription for file ID: " + fileId + ". Error: " + e.getMessage());
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
