package com.example.cabify.service;
import com.example.cabify.dto.ride.RideRequestDto;
import com.example.cabify.dto.ride.RideResponseDto;
import com.example.cabify.exception.ResourceNotFoundException;
import com.example.cabify.model.*;
import com.example.cabify.repository.DriverRepository;
import com.example.cabify.repository.RideRepository;
import com.example.cabify.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private static final Map<String, Double> routeDistances = new HashMap<>();
    static {
        // ALWAYS put the location that is first in the alphabet on the left!
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
        log.info("Booking request received - User ID: {}, Driver ID: {}, Route: {} to {}",
                request.getUserId(), request.getDriverId(), request.getSource(), request.getDestination());

        String src = request.getSource();
        String dest = request.getDestination();
        String routeKey = (src.compareTo(dest) < 0) ? src + "-" + dest : dest + "-" + src;

        Double distance = routeDistances.getOrDefault(routeKey, 15.0);

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

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error("Booking failed: User ID {} not found", request.getUserId());
                    return new ResourceNotFoundException("User not found with ID: " + request.getUserId());
                });

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> {
                    log.error("Booking failed: Driver ID {} not found", request.getDriverId());
                    return new ResourceNotFoundException("Driver not found with ID: " + request.getDriverId());
                });

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            log.warn("Booking failed: Driver {} is currently {}", request.getDriverId(), driver.getStatus());
            throw new IllegalStateException("Driver is currently " + driver.getStatus() + " and cannot accept rides.");
        }

        // Save Ride
        Ride ride = new Ride();
        ride.setUser(user);
        ride.setDriver(driver);
        ride.setSource(src);
        ride.setDestination(dest);
        ride.setFare(totalFare);
        ride.setStatus(RideStatus.BOOKED);
        ride.setStartTime(LocalDateTime.now());

        Ride savedRide = rideRepository.save(ride);

        //  Lock Driver
        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);

        // Combined Success Log
        log.info("Ride booked successfully. Ride ID: {}, Distance: {}km, Fare: {}", savedRide.getId(), distance, savedRide.getFare());

        return mapToDto(savedRide);
    }

    @Override
    @Transactional
    public RideResponseDto endRide(Long rideId) {
        log.info("Request to end Ride ID: {}", rideId); // Restored Log

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> {
                    log.error("End Ride failed: Ride ID {} not found", rideId); // Restored Log
                    return new ResourceNotFoundException("Ride not found with ID: " + rideId);
                });

        if (ride.getStatus() == RideStatus.COMPLETED) {
            log.warn("End Ride failed: Ride ID {} is already COMPLETED", rideId); // Restored Log
            throw new IllegalStateException("Ride is already completed!");
        }

        // Update Status
        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());
        rideRepository.save(ride);

        // Unlock Driver
        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);

        log.info("Ride ID {} completed successfully at {}", rideId, ride.getEndTime()); // Restored Log
        return mapToDto(ride);
    }

    @Override
    public List<RideResponseDto> getMyRides(Long userId) {
        // Validation with Logs
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Get Rides failed: User ID {} not found", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        List<Ride> rides = rideRepository.findByUser(userRepository.getById(userId));

        return rides.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // --- Helpers ---
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
