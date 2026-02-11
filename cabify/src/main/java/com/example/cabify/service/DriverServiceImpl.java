package com.example.cabify.service;

import com.example.cabify.dto.driver.DriverDto;
import com.example.cabify.dto.driver.DriverLoginRequestDto;
import com.example.cabify.dto.driver.DriverLoginResponseDto;
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
    public DriverDto registerDriver(DriverDto driverDto) {
        log.info("Registering new Driver with License: {}", driverDto.getLicenseNumber());
        
        // 1. Check for Null values from the DTO
        if (driverDto.getName() == null || driverDto.getLicenseNumber() == null || 
            driverDto.getVehicleDetails() == null || driverDto.getEmail() == null || 
            driverDto.getPassword() == null) {
            throw new IllegalArgumentException("All fields (Name, Email, Password, License, Vehicle) are required");
        }

        // 2. Trim and Sanitize data
        String name = driverDto.getName().trim();
        String email = driverDto.getEmail().trim().toLowerCase(); 
        String password = driverDto.getPassword(); 
        String license = driverDto.getLicenseNumber().trim();
        String vehicle = driverDto.getVehicleDetails().trim();
        String phone = (driverDto.getPhone() != null) ? driverDto.getPhone().trim() : "";

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
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // 4. Duplicate Check
        if (driverRepository.existsByLicenseNumber(license)) {
            log.error("Registration failed: License {} already used", license);
            throw new IllegalStateException("License Number is already registered!");
        }

        // 5. Map DTO to Entity for DB Storage
        Driver driverEntity = new Driver();
        driverEntity.setName(name);
        driverEntity.setEmail(email);
        driverEntity.setPassword(password); // In production, wrap this in BCrypt!
        driverEntity.setPhone(phone);
        driverEntity.setLicenseNumber(license);
        driverEntity.setVehicleDetails(vehicle);
        driverEntity.setStatus(DriverStatus.OFFLINE); // Default status

        // 6. Save Data
        Driver savedDriver = driverRepository.save(driverEntity);
        log.info("Driver registered successfully. ID: {}", savedDriver.getDriverId());
        
        return mapToDto(savedDriver);
    }
    @Override
    public DriverDto loginDriver(DriverLoginRequestDto loginRequest) {
        // 1. Find by Email
        Driver driver = driverRepository.findByEmail(loginRequest.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new NoSuchElementException("Invalid Email or Password"));

        // 2. Check Password
        if (!driver.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("Invalid Email or Password");
        }

        log.info("Driver authenticated: {}", loginRequest.getEmail());

        // 3. Return only the DriverDto
        return mapToDto(driver);
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
                driver.getEmail(),
                driver.getPassword(),
                driver.getPhone(),
                driver.getLicenseNumber(),
                driver.getVehicleDetails(),
                driver.getStatus()
        );
    }
}