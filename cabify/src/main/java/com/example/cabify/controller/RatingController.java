package com.example.cabify.controller;

import com.example.cabify.dto.rating.RatingRequestDto;
import com.example.cabify.dto.SuccessResponse; // Assuming this is the location from your screenshot
import com.example.cabify.model.Rating;
import com.example.cabify.service.IRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private IRatingService ratingService;

    @PostMapping
    public ResponseEntity<SuccessResponse<Rating>> submitRating(@RequestBody RatingRequestDto ratingDto) {
        Rating savedRating = ratingService.submitRating(ratingDto);

        // Fix: Use String, int, and Object to match your class signature
        SuccessResponse<Rating> response = new SuccessResponse<>(
                "Rating submitted successfully",
                201, // HTTP Created status code
                savedRating
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<SuccessResponse<List<Rating>>> getRatingsForUser(@PathVariable Long userId) {
        List<Rating> ratings = ratingService.getRatingsForUser(userId);

        // Fix: Use String, int, and List to match your class signature
        SuccessResponse<List<Rating>> response = new SuccessResponse<>(
                "Ratings retrieved successfully",
                200, // HTTP OK status code
                ratings
        );

        return ResponseEntity.ok(response);
    }
}