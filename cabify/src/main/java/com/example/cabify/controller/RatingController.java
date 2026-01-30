package com.example.cabify.controller;

import com.example.cabify.dto.rating.RatingRequestDto;
import com.example.cabify.dto.rating.RatingResponseDto;
import com.example.cabify.dto.SuccessResponse;
import com.example.cabify.service.IRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Added to match UserController style
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings/") // Added trailing slash to match UserController
public class RatingController {

    @Autowired
    private IRatingService ratingService;

    @PostMapping("submit") // Removed leading slash to match UserController style
    public ResponseEntity<SuccessResponse<RatingResponseDto>> submitRating(@RequestBody RatingRequestDto ratingDto) {
        RatingResponseDto responseData = ratingService.submitRating(ratingDto);

        SuccessResponse<RatingResponseDto> response = new SuccessResponse<>(
                "Rating submitted successfully",
                HttpStatus.CREATED.value(), // Using HttpStatus.CREATED.value() like UserController
                responseData
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED); // Using 'new ResponseEntity<>' style
    }

    @GetMapping("user/{userId}") // Removed leading slash to match UserController style
    public ResponseEntity<SuccessResponse<List<RatingResponseDto>>> getRatingsForUser(@PathVariable Long userId) {
        List<RatingResponseDto> ratings = ratingService.getRatingsForUser(userId);

        SuccessResponse<List<RatingResponseDto>> response = new SuccessResponse<>(
                "Ratings retrieved successfully",
                HttpStatus.OK.value(), // Using HttpStatus.OK.value()
                ratings
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}