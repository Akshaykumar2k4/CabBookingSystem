package com.example.cabify.controller;

import com.example.cabify.dto.rating.RatingRequestDto;
import com.example.cabify.dto.rating.RatingResponseDto;
import com.example.cabify.dto.SuccessResponse;
import com.example.cabify.service.IRatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings/")
public class RatingController {

    @Autowired
    private IRatingService ratingService;

    @PostMapping("submit")
    public ResponseEntity<?> submitRating(@Valid @RequestBody RatingRequestDto ratingDto) {
        try {
            // Log exactly what is arriving from the frontend
            System.out.println(">>> Incoming Rating Request:");
            System.out.println("Ride ID: " + ratingDto.getRideId());
            System.out.println("Passenger ID: " + ratingDto.getPassengerId());
            System.out.println("Score: " + ratingDto.getScore());

            RatingResponseDto responseData = ratingService.submitRating(ratingDto);

            SuccessResponse<RatingResponseDto> response = new SuccessResponse<>(
                    "Rating submitted successfully",
                    HttpStatus.CREATED.value(),
                    responseData
            );

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            // CRITICAL: This will print the full stack trace to your IDE console
            // so you can see which line in the Service failed.
            e.printStackTrace();

            // Return the specific error message to the frontend
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SuccessResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<SuccessResponse<List<RatingResponseDto>>> getRatingsForUser(@PathVariable Long userId) {
        List<RatingResponseDto> ratings = ratingService.getRatingsForUser(userId);

        SuccessResponse<List<RatingResponseDto>> response = new SuccessResponse<>(
                "Ratings retrieved successfully",
                HttpStatus.OK.value(),
                ratings
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}