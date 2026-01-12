package com.example.cabify.model;

public enum RideStatus {
    BOOKED,      // Driver assigned, waiting for pickup
    IN_PROGRESS, // Customer picked up, driving to destination
    COMPLETED,   // Reached destination
    CANCELLED    // Ride cancelled by user or driver
}