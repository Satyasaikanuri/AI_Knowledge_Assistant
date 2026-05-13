package com.knowledge.assistant.controller;

import com.knowledge.assistant.dto.AuthenticationRequest;
import com.knowledge.assistant.dto.AuthenticationResponse;
import com.knowledge.assistant.dto.RegisterRequest;
import com.knowledge.assistant.dto.TokenRefreshRequest;
import com.knowledge.assistant.dto.TokenRefreshResponse;
import com.knowledge.assistant.entity.RefreshToken;
import com.knowledge.assistant.security.JwtService;
import com.knowledge.assistant.service.AuthenticationService;
import com.knowledge.assistant.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthenticationController(
            AuthenticationService service,
            RefreshTokenService refreshTokenService,
            JwtService jwtService
    ) {
        this.service = service;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            // we could delete the refresh token based on the current user
        }
        return ResponseEntity.ok().build();
    }
}
