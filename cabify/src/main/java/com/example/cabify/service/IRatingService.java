package com.example.cabify.service;

import com.example.cabify.dto.rating.RatingRequestDto;
import com.example.cabify.model.Rating;
import java.util.List;

public interface IRatingService {
    // To handle POST /api/ratings [cite: 56, 57]
    Rating submitRating(RatingRequestDto ratingDto);

    // To handle GET /api/ratings/user/{userId} [cite: 58]
    List<Rating> getRatingsForUser(Long userId);
}