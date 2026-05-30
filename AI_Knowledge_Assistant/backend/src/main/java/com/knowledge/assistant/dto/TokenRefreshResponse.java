package com.knowledge.assistant.dto;


public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;

    public TokenRefreshResponse() {}

    public TokenRefreshResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static TokenRefreshResponseBuilder builder() {
        return new TokenRefreshResponseBuilder();
    }

    public static class TokenRefreshResponseBuilder {
        private String accessToken;
        private String refreshToken;

        public TokenRefreshResponseBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public TokenRefreshResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public TokenRefreshResponse build() {
            return new TokenRefreshResponse(accessToken, refreshToken);
        }
    }
}
