package com.example.cabify.service;

import com.example.cabify.dto.ride.RideRequestDto;
import com.example.cabify.dto.ride.RideResponseDto;
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
        // 1. Prepare Data
        RideRequestDto request = new RideRequestDto();
        request.setUserId(1L);
        request.setDriverId(101L);
        request.setSource("Adyar");
        request.setDestination("Guindy");

        User mockUser = new User();
        mockUser.setUserId(1L);

        Driver mockDriver = new Driver();
        mockDriver.setDriverId(101L);
        mockDriver.setStatus(DriverStatus.AVAILABLE);

        Ride mockSavedRide = new Ride();
        mockSavedRide.setId(500L);
        mockSavedRide.setFare(70.0);
        mockSavedRide.setDriver(mockDriver);
        mockSavedRide.setStatus(RideStatus.BOOKED);

        // 2. Train Mocks
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        Mockito.when(driverRepository.findById(101L)).thenReturn(Optional.of(mockDriver));
        Mockito.when(rideRepository.existsByUserAndStatus(mockUser, RideStatus.BOOKED)).thenReturn(false);
        Mockito.when(rideRepository.save(any(Ride.class))).thenReturn(mockSavedRide);

        // 3. Run
        RideResponseDto result = rideServiceImpl.bookRide(request);

        // 4. Verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(500L, result.getRideId());
        Mockito.verify(driverRepository).save(mockDriver);
    }

    // TEST 2: Check Locations List
    @Test
    public void testGetLocations() {
        List<String> locations = rideServiceImpl.getAvailableLocations();
        Assertions.assertNotNull(locations);
        Assertions.assertTrue(locations.contains("Adyar"));
        Assertions.assertTrue(locations.contains("Guindy"));
    }

    // TEST 3: Invalid Route (UPDATED MESSAGE)
    @Test
    public void testBookRide_InvalidRoute_ShouldThrowException() {
        RideRequestDto request = new RideRequestDto();
        request.setSource("Moon");
        request.setDestination("Mars");

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            rideServiceImpl.bookRide(request);
        });

        // Updated to match your validateAndFormat logic
        Assertions.assertTrue(exception.getMessage().contains("Invalid Location"));
    }

    // TEST 4: Driver is Busy
    @Test
    public void testBookRide_DriverBusy_ShouldThrowException() {
        RideRequestDto request = new RideRequestDto();
        request.setUserId(1L);
        request.setDriverId(101L);
        request.setSource("Adyar");
        request.setDestination("Guindy");

        User mockUser = new User();
        Driver busyDriver = new Driver();
        busyDriver.setDriverId(101L);
        busyDriver.setStatus(DriverStatus.BUSY);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        Mockito.when(driverRepository.findById(101L)).thenReturn(Optional.of(busyDriver));
        Mockito.when(rideRepository.existsByUserAndStatus(mockUser, RideStatus.BOOKED)).thenReturn(false);

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            rideServiceImpl.bookRide(request);
        });

        Assertions.assertTrue(exception.getMessage().contains("Driver is currently BUSY"));
    }

    // TEST 5: User Already Has a Ride (UPDATED SETUP)
    @Test
    public void testBookRide_UserAlreadyBusy_ShouldThrowException() {
        RideRequestDto request = new RideRequestDto();
        request.setUserId(1L);
        // ADDED: Driver ID to prevent "Driver Not Found" error
        request.setDriverId(101L);
        request.setSource("Adyar");
        request.setDestination("Guindy");

        User mockUser = new User();
        mockUser.setUserId(1L);

        // ADDED: Mock Driver to prevent crash
        Driver mockDriver = new Driver();
        mockDriver.setDriverId(101L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        Mockito.lenient().when(driverRepository.findById(101L)).thenReturn(Optional.of(mockDriver));

        // FORCE the User Busy Error
        Mockito.when(rideRepository.existsByUserAndStatus(mockUser, RideStatus.BOOKED)).thenReturn(true);

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            rideServiceImpl.bookRide(request);
        });

        Assertions.assertTrue(exception.getMessage().contains("already have an ongoing ride"));
    }
}