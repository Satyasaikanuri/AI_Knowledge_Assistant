package com.knowledge.assistant.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorSearchService {

    private final EmbeddingService embeddingService;

    public List<String> searchRelevantContext(String question, Long fileId, int maxResults) {
        log.info("Searching vector DB for question: '{}' in fileId: {}", question, fileId);

        EmbeddingModel embeddingModel = embeddingService.getEmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = embeddingService.getEmbeddingStore();

        dev.langchain4j.data.embedding.Embedding queryEmbedding = embeddingModel.embed(question).content();

        String fileUuid = embeddingService.getFileUuid(fileId);
        Filter fileFilter = new IsEqualTo("fileUuid", fileUuid);

        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(maxResults)
                .minScore(0.1)
                .filter(fileFilter)
                .build();

        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(request).matches();

        if (!matches.isEmpty()) {
            double topScore = matches.get(0).score();
            log.info("Top similarity score: {}", topScore);
        } else {
            log.info("No matching chunks found.");
        }

        List<String> results = matches.stream()
                .map(match -> {
                    String text = match.embedded().text();
                    String chunkIndex = match.embedded().metadata().getString("chunkIndex");
                    if (chunkIndex != null) {
                        return text + "\n[Source: Chunk " + chunkIndex + "]";
                    }
                    return text;
                })
                .distinct()
                .collect(Collectors.toList());

        log.info("Retrieved chunks: {}", results.size());

        return results;
    }
}
