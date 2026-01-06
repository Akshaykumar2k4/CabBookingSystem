package com.example.cabify.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SuccessResponse<T> {
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
    private T data;

    public SuccessResponse(String message, int statusCode, T data) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }
}