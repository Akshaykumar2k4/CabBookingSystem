package com.example.cabify.dto.rating;

import lombok.Data;

@Data
public class RatingRequestDto {
    private Long rideId;      // The ID of the completed ride
    private Long fromUserId;  // ID of the person giving the rating
    private Long toUserId;    // ID of the person being rated
    private Integer score;    // Rating value (1-5)
    private String comments;  // Feedback text
}