package com.example.cabify.dto.user;

public class AuthResponseDto {
    private final String token;

    public AuthResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}