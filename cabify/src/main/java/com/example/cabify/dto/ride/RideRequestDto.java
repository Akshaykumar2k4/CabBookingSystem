package com.example.cabify.dto.ride;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RideRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;       // Changed from 'long' to 'Long' to allow @NotNull check

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotBlank(message = "Source location is required")
    private String source;

    @NotBlank(message = "Destination is required")
    private String destination;

}