package com.example.cabify.service;

import com.example.cabify.dto.ride.RideRequestDto;
import com.example.cabify.dto.ride.RideResponseDto;
import com.example.cabify.exception.ResourceNotFoundException; // Import this
import com.example.cabify.model.*;
import com.example.cabify.repository.DriverRepository;
import com.example.cabify.repository.RideRepository;
import com.example.cabify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RideService implements IRideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    @Transactional
    public RideResponseDto bookRide(RideRequestDto request) {
        // 1. Fetch User (Throw 404 if not found)
        log.info("Booking request received - User ID: {}, Driver ID: {}", request.getUserId(), request.getDriverId()); // Log Entry
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error("Booking failed: User ID {} not found", request.getUserId()); // Log Error
                    return new ResourceNotFoundException("User not found with ID: " + request.getUserId());
                });

        // 2. Fetch Driver (Throw 404 if not found)
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> {
                    log.error("Booking failed: Driver ID {} not found", request.getDriverId()); // Log Error
                    return new ResourceNotFoundException("Driver not found with ID: " + request.getDriverId());
                });

        // 3. LOGIC CHECK: Is the driver actually free? (Throw 409 CONFLICT if busy)
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            log.warn("Booking failed: Driver {} is currently {}", request.getDriverId(), driver.getStatus()); // Log Warning (Business logic fail)
            throw new IllegalStateException("Driver is currently " + driver.getStatus() + " and cannot accept rides.");
        }

        // 4. Calculate Fare
        Double calculatedFare = calculateFare();

        // 5. Create and Save Ride
        Ride ride = new Ride();
        ride.setUser(user);
        ride.setDriver(driver);
        ride.setSource(request.getSource());
        ride.setDestination(request.getDestination());
        ride.setFare(calculatedFare);
        ride.setStatus(RideStatus.BOOKED);
        ride.setStartTime(LocalDateTime.now());

        Ride savedRide = rideRepository.save(ride);

        // 6. Lock the Driver
        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);
        log.info("Ride booked successfully. Ride ID: {}, Fare: {}", savedRide.getId(), savedRide.getFare()); // Log Success
        return mapToDto(savedRide);
    }

    @Override
    @Transactional
    public RideResponseDto endRide(Long rideId) {
        // 1. Find Ride (Throw 404 if not found)
        log.info("Request to end Ride ID: {}", rideId); // Log Entry
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> {
                    log.error("End Ride failed: Ride ID {} not found", rideId);
                    return new ResourceNotFoundException("Ride not found with ID: " + rideId);
                });

        // 2. Validate Status (Throw 409 CONFLICT if already done)
        if (ride.getStatus() == RideStatus.COMPLETED) {
            log.warn("End Ride failed: Ride ID {} is already COMPLETED", rideId);
            throw new IllegalStateException("Ride is already completed!");
        }

        // 3. Update Status
        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());
        rideRepository.save(ride);

        // 4. Unlock the Driver
        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);
        log.info("Ride ID {} completed successfully at {}", rideId, ride.getEndTime()); // Log Success
        return mapToDto(ride);
    }

    @Override
    public List<RideResponseDto> getMyRides(Long userId) {
        // 1. Validate User exists first
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<Ride> rides = rideRepository.findByUser(user);

        return rides.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // --- Helpers ---

    private Double calculateFare() {
        return 100 + (400 * new Random().nextDouble());
    }

    private RideResponseDto mapToDto(Ride ride) {
        RideResponseDto dto = new RideResponseDto();
        dto.setRideId(ride.getId());
        dto.setDriverName(ride.getDriver().getName());
        dto.setVehicleDetails(ride.getDriver().getVehicleDetails());
        dto.setSource(ride.getSource());
        dto.setDestination(ride.getDestination());
        dto.setStatus(ride.getStatus());
        dto.setFare(Math.round(ride.getFare() * 100.0) / 100.0);
        dto.setBookingTime(ride.getStartTime());
        return dto;
    }
}