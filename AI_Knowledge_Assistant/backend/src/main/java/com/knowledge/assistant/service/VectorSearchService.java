package com.knowledge.assistant.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VectorSearchService {

    private final EmbeddingService embeddingService;

    public VectorSearchService(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    public List<String> searchRelevantContext(String question, Long fileId, int maxResults) {
        System.out.println("Searching vector DB for question: '" + question + "' in fileId: " + fileId);

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
            System.out.println("Top similarity score: " + topScore);
        } else {
            System.out.println("No matching chunks found.");
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

        System.out.println("Retrieved chunks: " + results.size());

        return results;
    }
}
