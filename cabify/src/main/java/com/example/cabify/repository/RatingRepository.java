package com.example.cabify.repository;

import com.example.cabify.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // 1. Check if this PASSENGER has already rated this ride
    boolean existsByRideIdAndPassengerId(Long rideId, Long passengerId);

    // 2. Find all ratings given BY a specific passenger (User History)
    List<Rating> findByPassengerId(Long passengerId);

    // 3. Find all ratings received BY a specific driver (Driver History)
    List<Rating> findByDriverId(Long driverId);
}