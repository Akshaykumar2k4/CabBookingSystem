package com.example.cabify.service;

import com.example.cabify.dto.ride.RideRequestDto;
import com.example.cabify.dto.ride.RideResponseDto;
import com.example.cabify.exception.ResourceNotFoundException;
import com.example.cabify.model.*;
import com.example.cabify.repository.DriverRepository;
import com.example.cabify.repository.RideRepository;
import com.example.cabify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // 1. The Dataset (Distance Map)
    private static final Map<String, Double> routeDistances = new HashMap<>();

    static {
        // ALWAYS put the location that is first in the alphabet on the left!
        // This matches the "Sorting Machine" logic in the code.
        routeDistances.put("Adyar-Guindy", 7.0);
        routeDistances.put("Adyar-Marina", 36.0);
        routeDistances.put("Guindy-T-Nagar", 15.0);
        routeDistances.put("Kelambakkam-Siruseri", 6.0);
        routeDistances.put("Marina-T-Nagar", 24.0);
        routeDistances.put("Medavakkam-Sholinganallur", 8.0);
        routeDistances.put("Navalur-Sholinganallur", 5.0);
        routeDistances.put("Navalur-Siruseri", 7.0);
        routeDistances.put("Perungudi-Thoraipakkam", 3.0);
        routeDistances.put("Sholinganallur-Siruseri", 12.0);
    }

    @Override
    @Transactional
    public RideResponseDto bookRide(RideRequestDto request) {
        String src = request.getSource();
        String dest = request.getDestination();

        log.info("Booking request: {} to {}", src, dest);

        // 2. The Sorting Machine: Ensures "Siruseri-Navalur" becomes "Navalur-Siruseri"
        String routeKey = (src.compareTo(dest) < 0) ? src + "-" + dest : dest + "-" + src;

        // 3. Get Distance (Defaults to 15.0 km if route not found in dataset)
        Double distance = routeDistances.getOrDefault(routeKey, 15.0);

        // 4. Fare Calculation Logic (10, 9, 8, or 7 rupees per km)
        double ratePerKm;
        if (distance > 30) {
            ratePerKm = 7.0;
        } else if (distance > 20) {
            ratePerKm = 8.0;
        } else if (distance > 10) {
            ratePerKm = 9.0;
        } else {
            ratePerKm = 10.0;
        }

        double totalFare = distance * ratePerKm;

        // 5. Database Operations
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found ID: " + request.getUserId()));

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found ID: " + request.getDriverId()));

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new IllegalStateException("Driver is busy!");
        }

        Ride ride = new Ride();
        ride.setUser(user);
        ride.setDriver(driver);
        ride.setSource(src);
        ride.setDestination(dest);
        ride.setFare(totalFare);
        ride.setStatus(RideStatus.BOOKED);
        ride.setStartTime(LocalDateTime.now());

        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);

        log.info("Ride Saved: Distance {}km, Rate {}, Total Fare {}", distance, ratePerKm, totalFare);
        return mapToDto(rideRepository.save(ride));
    }

    @Override
    @Transactional
    public RideResponseDto endRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());

        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);

        return mapToDto(rideRepository.save(ride));
    }

    @Override
    public List<RideResponseDto> getMyRides(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return rideRepository.findByUser(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
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