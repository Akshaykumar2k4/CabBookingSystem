package com.example.cabify.service;

import com.example.cabify.dto.ride.RideRequestDto;
import com.example.cabify.dto.ride.RideResponseDto;
import java.util.List;

public interface IRideService {

    RideResponseDto bookRide(RideRequestDto request);

    RideResponseDto endRide(Long rideId);

    List<RideResponseDto> getMyRides(Long userId);

    List<String> getAvailableLocations();

    double calculateFare(String source, String destination);

    List<RideResponseDto> getMyRides(String email);

    public RideResponseDto getActiveRideForDriver(Long driverId);

    List<RideResponseDto> getDriverRideHistory(Long driverId);
}