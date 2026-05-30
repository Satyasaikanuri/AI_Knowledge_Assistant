package com.knowledge.assistant.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    // Simple in-memory storage for buckets
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> uploadBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> chatBuckets = new ConcurrentHashMap<>();

    public RateLimitingService() {
        // Manual constructor as requested (No Lombok)
    }

    /**
     * Resolve bucket for general API requests: 100 requests per minute
     */
    public Bucket resolveGeneralBucket(String ip) {
        return generalBuckets.computeIfAbsent(ip, key -> 
            Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                .build()
        );
    }

    /**
     * Resolve bucket for file uploads: 20 requests per hour
     */
    public Bucket resolveUploadBucket(String ip) {
        return uploadBuckets.computeIfAbsent(ip, key -> 
            Bucket.builder()
                .addLimit(Bandwidth.classic(20, Refill.greedy(20, Duration.ofHours(1))))
                .build()
        );
    }

    /**
     * Resolve bucket for chat API: 30 requests per minute
     */
    public Bucket resolveChatBucket(String identifier) {
        return chatBuckets.computeIfAbsent(identifier, key -> 
            Bucket.builder()
                .addLimit(Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1))))
                .build()
        );
    }
}
