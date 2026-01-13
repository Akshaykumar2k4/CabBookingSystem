package com.example.cabify.service;

import com.example.cabify.dto.ride.RideRequestDto;
import com.example.cabify.dto.ride.RideResponseDto;
import java.util.List;

public interface IRideService {

    // 1. User books a ride
    RideResponseDto bookRide(RideRequestDto request);

    // 2. Driver/User ends the ride
    RideResponseDto endRide(Long rideId);

    // 3. User views their history
    List<RideResponseDto> getMyRides(Long userId);
}