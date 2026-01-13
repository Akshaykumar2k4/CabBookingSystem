package com.example.cabify.dto.ride;

import lombok.Data;

@Data
public class RideRequestDto {
    private long userId;       // Who is booking?
    private Long driverId;     // Which driver?
    private String source;
    private String destination;
    // REMOVED: private Double fare; <-- System will decide this now!
}