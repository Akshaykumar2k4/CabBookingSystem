package com.example.cabify.dto.rating;

import lombok.Data;

@Data
public class RatingRequestDto {
    private Long rideId;      // ID of the ride being rated
    private Long fromUserId;  // ID of the user giving the feedback
    private Long toUserId;    // ID of the user receiving the feedback (usually the driver)
    private int score;        // Numerical rating (e.g., 1 to 5)
    private String comments;  // Optional text feedback
}