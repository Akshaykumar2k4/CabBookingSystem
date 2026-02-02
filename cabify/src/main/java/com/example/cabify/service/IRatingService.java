package com.example.cabify.service;

import com.example.cabify.dto.rating.RatingRequestDto;
import com.example.cabify.dto.rating.RatingResponseDto; // Update this import
import java.util.List;

public interface IRatingService {
    RatingResponseDto submitRating(RatingRequestDto ratingDto);
    List<RatingResponseDto> getRatingsForUser(Long userId);
}