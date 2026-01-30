package com.example.cabify.service;

import com.example.cabify.dto.rating.RatingRequestDto;
import com.example.cabify.dto.rating.RatingResponseDto; // Update this import
import java.util.List;

public interface IRatingService {
    // Change 'Rating' to 'RatingResponseDto'
    RatingResponseDto submitRating(RatingRequestDto ratingDto);

    // Update this to return a list of DTOs as well for consistency
    List<RatingResponseDto> getRatingsForUser(Long userId);
}