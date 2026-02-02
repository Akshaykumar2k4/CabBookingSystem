package com.example.cabify.service;

import com.example.cabify.dto.rating.RatingRequestDto;
import com.example.cabify.dto.rating.RatingResponseDto;
import com.example.cabify.exception.ResourceNotFoundException;
import com.example.cabify.model.Rating;
import com.example.cabify.model.Ride;
import com.example.cabify.model.RideStatus;
import com.example.cabify.repository.RatingRepository;
import com.example.cabify.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements IRatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RideRepository rideRepository;

    @Override
    @Transactional
    public RatingResponseDto submitRating(RatingRequestDto ratingDto) {
        Ride ride = rideRepository.findById(ratingDto.getRideId())
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        // 1. SECURITY: Match Passenger ID
        if (!ride.getUser().getUserId().equals(ratingDto.getPassengerId())) {
            throw new SecurityException("Unauthorized: You did not take this ride.");
        }

        // 2. STATUS CHECK
        if (ride.getStatus() != RideStatus.COMPLETED && ride.getStatus() != RideStatus.PAID) {
            throw new IllegalStateException("Ride is not finished yet.");
        }

        // 3. DUPLICATE CHECK (Using new method name)
        if (ratingRepository.existsByRideIdAndPassengerId(ratingDto.getRideId(), ratingDto.getPassengerId())) {
            throw new IllegalStateException("You have already rated this ride.");
        }

        // 4. SAVE (Mapping to new fields)
        Rating rating = new Rating();
        rating.setRide(ride);
        rating.setPassengerId(ratingDto.getPassengerId());
        rating.setDriverId(ride.getDriver().getDriverId()); // Auto-fill Driver ID
        rating.setScore(ratingDto.getScore());
        rating.setComments(ratingDto.getComments());

        Rating savedRating = ratingRepository.save(rating);
        return mapToResponseDto(savedRating);
    }

    @Override
    public List<RatingResponseDto> getRatingsForUser(Long userId) {
        // Assuming this endpoint is for a Passenger to see ratings they GAVE
        List<Rating> ratings = ratingRepository.findByPassengerId(userId);
        return ratings.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    private RatingResponseDto mapToResponseDto(Rating rating) {
        RatingResponseDto dto = new RatingResponseDto();
        dto.setRatingId(rating.getRatingId());
        dto.setRideId(rating.getRide().getId());
        dto.setPassengerId(rating.getPassengerId());
        dto.setDriverId(rating.getDriverId());
        dto.setScore(rating.getScore());
        dto.setComments(rating.getComments());
        dto.setCreatedAt(rating.getCreatedAt());
        return dto;
    }
}