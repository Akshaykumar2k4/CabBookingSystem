package com.example.cabify.dto.rating;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RatingResponseDto {
    private Long ratingId;
    private Long rideId;
    private Long passengerId;
    private Long driverId;
    private int score;
    private String comments;
    private LocalDateTime createdAt;
}