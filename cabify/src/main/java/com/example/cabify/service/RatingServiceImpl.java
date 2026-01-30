package com.example.cabify.service;

import com.example.cabify.dto.rating.RatingRequestDto;
import com.example.cabify.dto.rating.RatingResponseDto; // Added import
import com.example.cabify.model.Rating;
import com.example.cabify.model.Ride;
import com.example.cabify.model.RideStatus; // Added import for status check
import com.example.cabify.repository.RatingRepository;
import com.example.cabify.repository.RideRepository;
import com.example.cabify.exception.ResourceNotFoundException; // Recommended exception
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements IRatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RideRepository rideRepository;

    @Override
    public RatingResponseDto submitRating(RatingRequestDto ratingDto) {
        // 1. Fetch the ride and check status
        Ride ride = rideRepository.findById(ratingDto.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // 2. LOGIC CHECK: Only COMPLETED rides can be rated
        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new IllegalStateException("You can only rate a ride that is COMPLETED.");
        }

        // 3. Prevent duplicate ratings
        if (ratingRepository.existsByRideIdAndFromUserId(ratingDto.getRideId(), ratingDto.getFromUserId())) {
            throw new RuntimeException("You have already rated this ride.");
        }

        // 4. Map DTO to Entity and Save
        Rating rating = mapToEntity(ratingDto, ride);
        Rating savedRating = ratingRepository.save(rating);

        // 5. Map Saved Entity to Response DTO (The "Clean" Version)
        return mapToResponseDto(savedRating);
    }

    @Override
    public List<RatingResponseDto> getRatingsForUser(Long userId) {
        // 1. Fetch the list of entities from the database
        List<Rating> ratings = ratingRepository.findByToUserId(userId);

        // 2. Convert (Map) the list of Entities into a list of ResponseDtos
        return ratings.stream()
                .map(rating -> mapToResponseDto(rating))
                .collect(Collectors.toList());
    }

    // Helper: Request DTO -> Entity
    private Rating mapToEntity(RatingRequestDto dto, Ride ride) {
        Rating rating = new Rating();
        rating.setRide(ride);
        rating.setFromUserId(dto.getFromUserId());
        rating.setToUserId(dto.getToUserId());
        rating.setScore(dto.getScore());
        rating.setComments(dto.getComments());
        return rating;
    }

    // Helper: Entity -> Response DTO (New method)
    private RatingResponseDto mapToResponseDto(Rating rating) {
        RatingResponseDto responseDto = new RatingResponseDto();
        responseDto.setRatingId(rating.getRatingId());
        responseDto.setRideId(rating.getRide().getId()); // Just the ID!
        responseDto.setScore(rating.getScore());
        responseDto.setComments(rating.getComments());
        responseDto.setCreatedAt(rating.getCreatedAt());
        return responseDto;
    }
}