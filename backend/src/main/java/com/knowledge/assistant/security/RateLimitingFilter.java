package com.knowledge.assistant.security;

import com.knowledge.assistant.config.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;

    public RateLimitingFilter(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();

        // Determine which bucket to use based on the endpoint
        Bucket bucket;

        if (path.contains("/api/v1/files/upload")) {
            bucket = rateLimitingService.resolveUploadBucket(ip);
        } else if (path.contains("/api/v1/chat/ask")) {
            // For chat, we could ideally use the user identifier from JWT, 
            // but since this filter runs before/around JWT validation, 
            // we'll stick to IP for now or let the service handle the identifier.
            bucket = rateLimitingService.resolveChatBucket(ip);
        } else {
            bucket = rateLimitingService.resolveGeneralBucket(ip);
        }

        // Try to consume a token
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            System.out.println("Rate limit exceeded for IP: " + ip + " on path: " + path);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\", \"status\": 429}");
        }
    }
}
