package com.example.cabify.service;

import com.example.cabify.dto.driver.DriverDto;
import com.example.cabify.model.Driver;
import com.example.cabify.model.DriverStatus;
import com.example.cabify.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DriverServiceImpl implements IDriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public DriverDto registerDriver(Driver driver) {
        log.info("Registering new Driver with License: {}", driver.getLicenseNumber());
        // 1. Check for Null values
        if (driver.getName() == null || driver.getLicenseNumber() == null || driver.getVehicleDetails() == null) {
            throw new IllegalArgumentException("Name, License Number, and Vehicle Details cannot be empty");
        }

        // 2. Trim and Sanitize data
        String name = driver.getName().trim();
        String license = driver.getLicenseNumber().trim();
        String vehicle = driver.getVehicleDetails().trim();
        // Handle phone safely (check for null first if needed, though Entity usually handles binding)
        String phone = (driver.getPhone() != null) ? driver.getPhone().trim() : "";

        // 3. Validation Logic
        if (name.length() < 3 || name.length() > 30) {
            throw new IllegalArgumentException("Driver name should be between 3 to 30 characters");
        }

        if (phone.length() != 10 || !phone.matches("\\d+")) {
            throw new IllegalArgumentException("Provide a valid 10-digit phone number");
        }

        if (license.length() < 5) {
            throw new IllegalArgumentException("Invalid License Number format");
        }

        // 4. Duplicate Check
        if (driverRepository.existsByLicenseNumber(license)) {
            log.error("Driver registration failed: License {} already used", driver.getLicenseNumber());
            throw new IllegalStateException("Driver with this License Number is already registered!");
        }

        // 5. Save Data (Set Defaults)
        driver.setName(name);
        driver.setPhone(phone);
        driver.setLicenseNumber(license);
        driver.setVehicleDetails(vehicle);
        driver.setStatus(DriverStatus.OFFLINE); // Default status is OFFLINE

        Driver savedDriver = driverRepository.save(driver);
        log.info("Driver registered successfully. ID: {}", savedDriver.getDriverId());
        // 6. Return DTO
        return mapToDto(savedDriver);
    }

    @Override
    public DriverDto getDriverById(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new NoSuchElementException("Driver not found with ID: " + driverId));
        return mapToDto(driver);
    }

    @Override
    public DriverDto updateDriverStatus(Long driverId, String statusStr) {
        log.info("Request to update status for Driver ID: {} to {}", driverId, statusStr);
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new NoSuchElementException("Driver not found with ID: " + driverId));

        try {
            // Converts string input (e.g., "available") to Enum (AVAILABLE)
            DriverStatus newStatus = DriverStatus.valueOf(statusStr.toUpperCase());
            driver.setStatus(newStatus);
            log.info("Driver ID {} status updated to {}", driverId, newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Use: AVAILABLE, BUSY, or OFFLINE");
        }

        Driver updatedDriver = driverRepository.save(driver);
        return mapToDto(updatedDriver);
    }

    @Override
    public List<DriverDto> getAvailableDrivers() {
        List<Driver> drivers = driverRepository.findByStatus(DriverStatus.AVAILABLE);

        if (drivers.isEmpty()) {
            throw new NoSuchElementException("No available drivers found at the moment");
        }

        return drivers.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Helper method to convert Entity -> DTO
    private DriverDto mapToDto(Driver driver) {
        return new DriverDto(
                driver.getDriverId(),
                driver.getName(),
                driver.getPhone(),
                driver.getLicenseNumber(),
                driver.getVehicleDetails(),
                driver.getStatus()
        );
    }
}