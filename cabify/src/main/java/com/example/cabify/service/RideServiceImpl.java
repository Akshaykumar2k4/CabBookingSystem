package com.example.cabify.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cabify.dto.ride.RideRequestDto;
import com.example.cabify.dto.ride.RideResponseDto;
import com.example.cabify.exception.ResourceNotFoundException;
import com.example.cabify.model.Driver;
import com.example.cabify.model.DriverStatus;
import com.example.cabify.model.Payment;
import com.example.cabify.model.PaymentMethod;
import com.example.cabify.model.PaymentStatus;
import com.example.cabify.model.Ride;
import com.example.cabify.model.RideStatus;
import com.example.cabify.model.User;
import com.example.cabify.repository.DriverRepository;
import com.example.cabify.repository.RideRepository;
import com.example.cabify.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
public class RideServiceImpl implements IRideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;
    private static final Map<String, Double> routeDistances = new HashMap<>();
    static {
        // --- 8 MAJOR HUBS
        routeDistances.put("Adyar-AnnaNagar", 14.0);
        routeDistances.put("Adyar-Guindy", 7.0);
        routeDistances.put("Adyar-Marina", 8.0);
        routeDistances.put("Adyar-Sholinganallur", 14.0);
        routeDistances.put("Adyar-Tambaram", 18.0);
        routeDistances.put("Adyar-TNagar", 6.0);
        routeDistances.put("Adyar-Velachery", 5.0);

        // 2. Anna Nagar Connections
        routeDistances.put("AnnaNagar-Guindy", 11.0);
        routeDistances.put("AnnaNagar-Marina", 12.0);
        routeDistances.put("AnnaNagar-Sholinganallur", 26.0);
        routeDistances.put("AnnaNagar-Tambaram", 22.0);
        routeDistances.put("AnnaNagar-TNagar", 9.0);
        routeDistances.put("AnnaNagar-Velachery", 16.0);

        // 3. Guindy Connections
        routeDistances.put("Guindy-Marina", 12.0);
        routeDistances.put("Guindy-Sholinganallur", 16.0);
        routeDistances.put("Guindy-Tambaram", 14.0);
        routeDistances.put("Guindy-TNagar", 6.0);
        routeDistances.put("Guindy-Velachery", 4.0);

        // 4. Marina Connections
        routeDistances.put("Marina-Sholinganallur", 22.0);
        routeDistances.put("Marina-Tambaram", 26.0);
        routeDistances.put("Marina-TNagar", 7.0);
        routeDistances.put("Marina-Velachery", 13.0);

        // 5. Sholinganallur Connections (The OMR Hub)
        routeDistances.put("Sholinganallur-Tambaram", 14.0);
        routeDistances.put("Sholinganallur-TNagar", 19.0);
        routeDistances.put("Sholinganallur-Velachery", 10.0);

        // 6. Tambaram Connections
        routeDistances.put("Tambaram-TNagar", 18.0);
        routeDistances.put("Tambaram-Velachery", 14.0);

        // 7. TNagar Connections
        routeDistances.put("TNagar-Velachery", 9.0);

        // --- OMR LOCALS (Short Distance Connectors) ---
        routeDistances.put("Kelambakkam-Siruseri", 5.0);
        routeDistances.put("Navalur-Siruseri", 7.0);
        routeDistances.put("Navalur-Sholinganallur", 5.0);
        routeDistances.put("Medavakkam-Sholinganallur", 8.0);
        routeDistances.put("Perungudi-Thoraipakkam", 3.0);
        routeDistances.put("Perungudi-Velachery", 4.0);
        routeDistances.put("Thoraipakkam-Sholinganallur", 6.0);
    }

    @Override
    public double calculateFare(String source, String destination) {
        String src = validateAndFormat(source);
        String dest = validateAndFormat(destination);
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
        return Math.round((distance * ratePerKm) * 100.0) / 100.0;
    }

    @Override
    @Transactional
    public RideResponseDto bookRide(RideRequestDto request) {
        // ⚠️ FIXED: Removed driverId from logs
        log.info("Booking request received - User ID: {}, Route: {} to {}",
                request.getUserId(), request.getSource(), request.getDestination());

        String src = validateAndFormat(request.getSource());
        String dest = validateAndFormat(request.getDestination());
        
        double totalFare = calculateFare(src, dest);
        
        String routeKey = (src.compareTo(dest) < 0) ? src + "-" + dest : dest + "-" + src;
        Double distance = routeDistances.getOrDefault(routeKey, 15.0); 

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error("Booking failed: User ID {} not found", request.getUserId());
                    return new ResourceNotFoundException("User not found with ID: " + request.getUserId());
                });

        // ⚠️ FIXED: Auto-Assign Driver (Use findFirstByStatus instead of findById)
        Driver driver = driverRepository.findFirstByStatus(DriverStatus.AVAILABLE)
                .orElseThrow(() -> {
                    log.error("Booking failed: No drivers available");
                    return new ResourceNotFoundException("No cabs are currently available! Please try again later.");
                });
        
        if (rideRepository.existsByUserAndStatus(user, RideStatus.BOOKED)) {
            log.warn("Booking failed: User ID {} already has an ongoing ride", request.getUserId());
            throw new IllegalStateException("You already have an ongoing ride! Complete it before booking a new one.");
        }
        
        // Removed the check for driver status because we specifically fetched an AVAILABLE one above.

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

        // Lock Driver
        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);

        // Combined Success Log
        log.info("Ride booked successfully. Ride ID: {}, Distance: {}km, Fare: {}", savedRide.getId(), distance, savedRide.getFare());

        return mapToDto(savedRide);
    }

    @Override
    @Transactional
    public RideResponseDto endRide(Long rideId) {
        log.info("Request to end Ride ID: {}", rideId); 

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> {
                    log.error("End Ride failed: Ride ID {} not found", rideId); 
                    return new ResourceNotFoundException("Ride not found with ID: " + rideId);
                });

        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.PAID) {
            log.warn("End Ride failed: Ride ID {} is already COMPLETED or PAID", rideId); 
            throw new IllegalStateException("Ride is already completed!");
        }

        // 1. Update Ride Status
        ride.setStatus(RideStatus.PAID);
        ride.setEndTime(LocalDateTime.now());

        Payment payment = new Payment();
        payment.setRide(ride); // Sync side 1
        payment.setUser(ride.getUser());
        payment.setAmount(ride.getFare());
        payment.setPaymentMethod(PaymentMethod.UPI); // Or whatever default/logic you use
        payment.setStatus(PaymentStatus.SUCCESS);
        ride.setPayment(payment);

        rideRepository.save(ride);

        // 2. Unlock Driver
        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);

        log.info("Ride ID {} completed successfully. Payment generated.", rideId); 
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
    @Override
public RideResponseDto getActiveRideForDriver(Long driverId) {
    // 1. Verify driver exists
    driverRepository.findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

    // 2. Fetch the active ride from the custom query
    return rideRepository.findActiveRideByDriver(driverId)
            .map(this::mapToDto)
            .orElse(null); // Return null if no ride exists (this prevents the 404 crash)
}

    // --- Helpers ---
    private RideResponseDto mapToDto(Ride ride) {
    RideResponseDto dto = new RideResponseDto();
    dto.setRideId(ride.getId());
    
   
    dto.setUserName(ride.getUser().getName()); 
    
    dto.setDriverName(ride.getDriver().getName());
    dto.setVehicleModel(ride.getDriver().getVehicleModel());
    dto.setVehiclePlate(ride.getDriver().getVehiclePlate());
    dto.setSource(ride.getSource());
    dto.setDestination(ride.getDestination());
    dto.setStatus(ride.getStatus());
    dto.setFare(Math.round(ride.getFare() * 100.0) / 100.0);
    dto.setBookingTime(ride.getStartTime());
    return dto;
}
    

    @Override
    public List<RideResponseDto> getDriverRideHistory(Long driverId) {
        // 1. Verify driver exists
        driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

        // 2. Fetch all rides for the driver
        List<Ride> rides = rideRepository.findByDriver(driverRepository.getById(driverId));

        // 3. Return list of DTOs
        return rides.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAvailableLocations() {
        return routeDistances.keySet().stream()
                .flatMap(key -> java.util.Arrays.stream(key.split("-")))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<RideResponseDto> getMyRides(String email) {
        // 1. Find User by Email (Business Logic)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // 2. Reuse existing logic to get rides
        return getMyRides(user.getUserId());
    }

    private String validateAndFormat(String input) {
        List<String> validLocations = getAvailableLocations();
        return validLocations.stream()
                .filter(loc -> loc.equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Location: " + input));
    }

}