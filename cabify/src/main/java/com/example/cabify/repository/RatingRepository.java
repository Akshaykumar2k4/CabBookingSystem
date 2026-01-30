package com.example.cabify.repository;

import com.example.cabify.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * Finds all ratings given to a specific user or driver.
     * If a driver rates a user, toUserId will be the User's ID.
     */
    List<Rating> findByToUserId(Long toUserId);

    /**
     * Checks if a rating already exists for a specific ride.
     * Prevents double-rating for the same trip.
     */
    boolean existsByRideIdAndFromUserId(Long rideId, Long fromUserId);
}