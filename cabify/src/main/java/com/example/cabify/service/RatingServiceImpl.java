package com.example.cabify.service;

import com.example.cabify.dto.rating.RatingRequestDto;
import com.example.cabify.model.Rating;
import com.example.cabify.model.Ride;
import com.example.cabify.repository.RatingRepository;
import com.example.cabify.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RatingServiceImpl implements IRatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RideRepository rideRepository;

    @Override
    public Rating submitRating(RatingRequestDto ratingDto) {
        // 1. Check if the ride exists [cite: 55]
        Ride ride = rideRepository.findById(ratingDto.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // 2. Prevent duplicate ratings
        if (ratingRepository.existsByRideIdAndFromUserId(ratingDto.getRideId(), ratingDto.getFromUserId())) {
            throw new RuntimeException("You have already rated this ride.");
        }

        // 3. Map DTO to Entity using the private helper method
        Rating rating = mapToEntity(ratingDto, ride);

        return ratingRepository.save(rating); // [cite: 57]
    }

    @Override
    public List<Rating> getRatingsForUser(Long userId) {
        return ratingRepository.findByToUserId(userId); // [cite: 58]
    }

    // Helper method to handle mapping logic
    private Rating mapToEntity(RatingRequestDto dto, Ride ride) {
        Rating rating = new Rating();
        rating.setRide(ride); // [cite: 55]
        rating.setFromUserId(dto.getFromUserId()); // [cite: 55]
        rating.setToUserId(dto.getToUserId()); // [cite: 55]
        rating.setScore(dto.getScore()); // [cite: 55]
        rating.setComments(dto.getComments()); // [cite: 55]
        return rating;
    }
}