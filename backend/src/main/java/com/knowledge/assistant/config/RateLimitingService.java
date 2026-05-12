package com.knowledge.assistant.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RateLimitingService {

    private final ProxyManager<String> proxyManager;

    // 100 requests per minute per IP
    public Bucket resolveGeneralBucket(String ip) {
        return proxyManager.builder().build("general_" + ip, () -> getConfiguration(100, Duration.ofMinutes(1)));
    }

    // 20 upload requests per hour per IP
    public Bucket resolveUploadBucket(String ip) {
        return proxyManager.builder().build("upload_" + ip, () -> getConfiguration(20, Duration.ofHours(1)));
    }

    // 30 chat requests per minute per IP/User
    public Bucket resolveChatBucket(String identifier) {
        return proxyManager.builder().build("chat_" + identifier, () -> getConfiguration(30, Duration.ofMinutes(1)));
    }

    private BucketConfiguration getConfiguration(long capacity, Duration period) {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.greedy(capacity, period)))
                .build();
    }
}
