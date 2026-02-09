package com.example.cabify.controller;

import com.example.cabify.dto.ride.RideRequestDto;
import com.example.cabify.dto.ride.RideResponseDto;
import com.example.cabify.dto.SuccessResponse;
import com.example.cabify.service.IRideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private IRideService rideService;

    // 1. Book a Ride
    @PostMapping("/book")
    public ResponseEntity<SuccessResponse<RideResponseDto>> bookRide(@Valid @RequestBody RideRequestDto request) {
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

    @GetMapping("/locations")
public ResponseEntity<SuccessResponse<List<String>>> getLocations() {
    List<String> locations = rideService.getAvailableLocations();
    SuccessResponse<List<String>> response = new SuccessResponse<>(
            "Locations fetched successfully",
            HttpStatus.OK.value(),
            locations
    );
    return ResponseEntity.ok(response);
}

    // 🆕 ESTIMATE FARE (Wrapped in SuccessResponse)
    @GetMapping("/estimate")
    public ResponseEntity<SuccessResponse<Double>> estimateFare(@RequestParam String source, @RequestParam String destination) {
        Double fare = rideService.calculateFare(source, destination);

        SuccessResponse<Double> successResponse = new SuccessResponse<>(
                "Fare estimated successfully",
                HttpStatus.OK.value(),
                fare
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<SuccessResponse<List<RideResponseDto>>> getMyHistory(Principal principal) {
        // 1. Get the email from the secure Token
        String email = principal.getName();
        
        // 2. Ask the Service for the rides
        List<RideResponseDto> rides = rideService.getMyRides(email);

        // 3. Return Success Response
        SuccessResponse<List<RideResponseDto>> response = new SuccessResponse<>(
                "User history fetched successfully",
                HttpStatus.OK.value(),
                rides
        );
        return ResponseEntity.ok(response);
    }
}