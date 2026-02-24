package com.example.cabify.dto.ride;

import com.example.cabify.model.RideStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RideResponseDto {
    private Long rideId;
    private String userName; 
    private String driverName;     
    private String vehicleModel; 
    private String vehiclePlate;
    private String source;
    private String destination;
    private RideStatus status;      
    private Double fare;
    private LocalDateTime bookingTime;
}