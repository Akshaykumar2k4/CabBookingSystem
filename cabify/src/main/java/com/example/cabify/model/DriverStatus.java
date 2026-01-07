package com.example.cabify.model;

public enum DriverStatus {
    AVAILABLE,  // Driver is free and ready to accept a ride
    BUSY,       // Driver is currently in a ride
    OFFLINE     // Driver has ended their shift
}