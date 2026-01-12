package com.example.cabify.dto.ride;

import com.example.cabify.model.RideStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RideResponseDto {
    private Long rideId;
    private String driverName;      // Converted from Driver ID
    private String vehicleDetails;  // Converted from Driver ID
    private String source;
    private String destination;
    private RideStatus status;      // e.g., BOOKED
    private Double fare;
    private LocalDateTime bookingTime;
}