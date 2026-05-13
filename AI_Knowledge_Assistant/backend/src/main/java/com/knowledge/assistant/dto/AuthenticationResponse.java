package com.knowledge.assistant.dto;


public class AuthenticationResponse {
    private String token;
    private String refreshToken;
    private UserDto user;

    public AuthenticationResponse() {}

    public AuthenticationResponse(String token, String refreshToken, UserDto user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public static AuthenticationResponseBuilder builder() {
        return new AuthenticationResponseBuilder();
    }

    public static class AuthenticationResponseBuilder {
        private String token;
        private String refreshToken;
        private UserDto user;

        public AuthenticationResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthenticationResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public AuthenticationResponseBuilder user(UserDto user) {
            this.user = user;
            return this;
        }

        public AuthenticationResponse build() {
            return new AuthenticationResponse(token, refreshToken, user);
        }
    }
}
