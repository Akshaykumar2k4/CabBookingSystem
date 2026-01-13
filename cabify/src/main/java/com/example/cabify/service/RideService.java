package com.example.cabify.service;

import com.example.cabify.dto.ride.RideRequestDto;
import com.example.cabify.dto.ride.RideResponseDto;
import com.example.cabify.model.*;
import com.example.cabify.repository.DriverRepository;
import com.example.cabify.repository.RideRepository;
import com.example.cabify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class RideService implements IRideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    // --- 1. BOOK A RIDE ---
    @Override
    @Transactional // CRITICAL: Ensures Ride is saved AND Driver is locked together
    public RideResponseDto bookRide(RideRequestDto request) {
        // 1. Fetch User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Fetch Driver
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // 3. LOGIC CHECK: Is the driver actually free?
        if (driver.getStatus()!=DriverStatus.AVAILABLE) {
            throw new RuntimeException("Driver is not available!");
        }

        // 4. Calculate Fare (System Logic)
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

        // 6. LOCK THE DRIVER
        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);

        return mapToDto(savedRide);
    }

    // --- 2. END A RIDE ---
    @Override
    @Transactional // CRITICAL: Updates both Ride table and Driver table
    public RideResponseDto endRide(Long rideId) {
        // 1. Find the ride
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // 2. Validate: Can't end a ride that's already done
        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new RuntimeException("Ride is already completed!");
        }

        // 3. Update Ride Status
        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());
        rideRepository.save(ride);

        // 4. UNLOCK THE DRIVER (Make them available again)
        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);

        return mapToDto(ride);
    }

    // --- 3. GET RIDE HISTORY ---
    @Override
    public List<RideResponseDto> getMyRides(long userId) {
        // 1. Find the User (to ensure they exist)
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Fetch all rides for this user from DB
        List<Ride> rides = rideRepository.findByUser(user);

        // 3. Convert the list of Entities -> List of DTOs
        return rides.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // --- HELPER METHODS ---

    private Double calculateFare() {
        // Random price between 100 and 500
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
        dto.setFare(Math.round(ride.getFare() * 100.0) / 100.0); // Round to 2 decimals
        dto.setBookingTime(ride.getStartTime());
        return dto;
    }
}