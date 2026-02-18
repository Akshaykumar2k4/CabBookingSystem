package com.example.cabify.service;

import com.example.cabify.dto.ride.RideRequestDto;
import com.example.cabify.dto.ride.RideResponseDto;
import com.example.cabify.exception.ResourceNotFoundException;
import com.example.cabify.model.*;
import com.example.cabify.repository.DriverRepository;
import com.example.cabify.repository.RideRepository;
import com.example.cabify.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class RideServiceImplTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private RideServiceImpl rideServiceImpl;

    // TEST 1: Successful Booking
    @Test
    public void testBookRide_Success() {
        RideRequestDto request = new RideRequestDto();
        request.setUserId(1L);
        request.setSource("Adyar");
        request.setDestination("Guindy");

        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setName("Akshay");

        Driver mockDriver = new Driver();
        mockDriver.setDriverId(101L);
        mockDriver.setStatus(DriverStatus.AVAILABLE);

        Ride mockSavedRide = new Ride();
        mockSavedRide.setId(500L);
        mockSavedRide.setFare(70.0);
        mockSavedRide.setDriver(mockDriver);
        mockSavedRide.setStatus(RideStatus.BOOKED);
        mockSavedRide.setUser(mockUser); // Prevents NPE

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        Mockito.when(driverRepository.findFirstByStatus(DriverStatus.AVAILABLE))
                .thenReturn(Optional.of(mockDriver));
        Mockito.when(rideRepository.existsByUserAndStatus(mockUser, RideStatus.BOOKED)).thenReturn(false);
        Mockito.when(rideRepository.save(any(Ride.class))).thenReturn(mockSavedRide);

        RideResponseDto result = rideServiceImpl.bookRide(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(500L, result.getRideId());
        Mockito.verify(driverRepository).findFirstByStatus(DriverStatus.AVAILABLE);
    }

    // TEST 2: Check Locations List
    @Test
    public void testGetLocations() {
        List<String> locations = rideServiceImpl.getAvailableLocations();
        Assertions.assertNotNull(locations);
        Assertions.assertTrue(locations.contains("Adyar"));
        Assertions.assertTrue(locations.contains("Guindy"));
    }

    // TEST 3: Invalid Route
    @Test
    public void testBookRide_InvalidRoute_ShouldThrowException() {
        RideRequestDto request = new RideRequestDto();
        request.setUserId(1L);
        request.setSource("Moon");
        request.setDestination("Mars");

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            rideServiceImpl.bookRide(request);
        });

        Assertions.assertTrue(exception.getMessage().contains("Invalid Location"));
    }

    // TEST 4: No Drivers Available (FIXED)
    @Test
    public void testBookRide_NoDriversAvailable_ShouldThrowException() {
        RideRequestDto request = new RideRequestDto();
        request.setUserId(1L);
        request.setSource("Adyar");
        request.setDestination("Guindy");

        User mockUser = new User();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // REMOVED: Mockito.when(rideRepository.existsByUserAndStatus(...))
        // REASON: The code fails at the driver check before reaching the ride check.

        Mockito.when(driverRepository.findFirstByStatus(DriverStatus.AVAILABLE))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            rideServiceImpl.bookRide(request);
        });

        Assertions.assertTrue(exception.getMessage().contains("No cabs are currently available"));
    }

    // TEST 5: User Already Has a Ride
    @Test
    public void testBookRide_UserAlreadyBusy_ShouldThrowException() {
        RideRequestDto request = new RideRequestDto();
        request.setUserId(1L);
        request.setSource("Adyar");
        request.setDestination("Guindy");

        User mockUser = new User();
        mockUser.setUserId(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // We must provide a driver so the code proceeds to the "User Busy" check
        Mockito.when(driverRepository.findFirstByStatus(DriverStatus.AVAILABLE))
                .thenReturn(Optional.of(new Driver()));

        Mockito.when(rideRepository.existsByUserAndStatus(mockUser, RideStatus.BOOKED)).thenReturn(true);

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            rideServiceImpl.bookRide(request);
        });

        Assertions.assertTrue(exception.getMessage().contains("already have an ongoing ride"));
    }
}