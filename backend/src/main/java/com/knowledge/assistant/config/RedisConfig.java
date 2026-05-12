package com.knowledge.assistant.config;

import io.github.bucket4j.distributed.expiration.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    @Bean
    public ProxyManager<String> proxyManager() {
        // Simple URI for local/docker redis
        RedisClient redisClient = RedisClient.create(RedisURI.create("redis://redis:6379"));
        
        ClientSideConfig clientSideConfig = ClientSideConfig.withExpirationAfterWriteStrategy(
                ExpirationAfterWriteStrategy.fixedTtl(Duration.ofDays(1))
        );

        return LettuceBasedProxyManager.builderFor(redisClient)
                .withClientSideConfig(clientSideConfig)
                .build();
    }
}
