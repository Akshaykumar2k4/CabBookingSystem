package com.example.cabify.controller;

import com.example.cabify.dto.ride.RideRequestDto;
import com.example.cabify.dto.ride.RideResponseDto;
import com.example.cabify.dto.SuccessResponse;
import com.example.cabify.service.IRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private IRideService rideService;

    // 1. Book a Ride
    @PostMapping("/book")
    public ResponseEntity<SuccessResponse<RideResponseDto>> bookRide(@RequestBody RideRequestDto request) {
        RideResponseDto response = rideService.bookRide(request);

        // Passing message, 201 status code, and the data object
        SuccessResponse<RideResponseDto> successResponse = new SuccessResponse<>(
                "Ride booked successfully",
                HttpStatus.CREATED.value(),
                response
        );

        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    // 2. End a Ride
    @PutMapping("/{rideId}/end")
    public ResponseEntity<SuccessResponse<RideResponseDto>> endRide(@PathVariable Long rideId) {
        RideResponseDto response = rideService.endRide(rideId);

        // Passing message, 200 status code, and the data object
        SuccessResponse<RideResponseDto> successResponse = new SuccessResponse<>(
                "Ride ended successfully",
                HttpStatus.OK.value(),
                response
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    // 3. Get History
    @GetMapping("/history/{userId}")
    public ResponseEntity<SuccessResponse<List<RideResponseDto>>> getHistory(@PathVariable Long userId) {
        List<RideResponseDto> history = rideService.getMyRides(userId);

        // Passing message, 200 status code, and the list of data
        SuccessResponse<List<RideResponseDto>> successResponse = new SuccessResponse<>(
                "Ride history fetched successfully",
                HttpStatus.OK.value(),
                history
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }
}