package com.example.cabify.service;

import com.example.cabify.dto.driver.DriverDto;
import com.example.cabify.dto.driver.DriverLoginRequestDto;
import com.example.cabify.model.Driver;
import com.example.cabify.model.DriverStatus;
import com.example.cabify.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DriverServiceImpl implements IDriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public DriverDto registerDriver(DriverDto driverDto) {
        log.info("Registering new Driver with License: {}", driverDto.getLicenseNumber());
        
        // 1. Updated Null Checks for split vehicle fields
        if (driverDto.getName() == null || driverDto.getLicenseNumber() == null || 
            driverDto.getVehicleModel() == null || driverDto.getVehiclePlate() == null || 
            driverDto.getEmail() == null || driverDto.getPassword() == null) {
            throw new IllegalArgumentException("All fields (Name, Email, Password, License, Vehicle Model/Plate) are required");
        }

        // 2. Trim and Sanitize data
        String name = driverDto.getName().trim();
        String email = driverDto.getEmail().trim().toLowerCase(); 
        String password = driverDto.getPassword(); 
        String license = driverDto.getLicenseNumber().trim();
        String vModel = driverDto.getVehicleModel().trim();
        String vPlate = driverDto.getVehiclePlate().trim();
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

        // 4. Specific Duplicate Checks
        if (driverRepository.existsByEmail(email)) {
            log.error("Registration failed: Email {} already used", email);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered!");
        }

        if (driverRepository.existsByPhone(phone)) {
            log.error("Registration failed: Phone {} already used", phone);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number is already registered!");
        }

        if (driverRepository.existsByLicenseNumber(license)) {
            log.error("Registration failed: License {} already used", license);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "License Number is already registered!");
        }

        if (driverRepository.existsByVehiclePlate(vPlate)) {
            log.error("Registration failed: Vehicle Plate {} already used", vPlate);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle Plate is already registered to another driver!");
        }

        // 5. Map DTO to Entity for DB Storage
        Driver driverEntity = new Driver();
        driverEntity.setName(name);
        driverEntity.setEmail(email);
        driverEntity.setPassword(passwordEncoder.encode(password)); 
        driverEntity.setPhone(phone);
        driverEntity.setLicenseNumber(license);
        
        // 🚀 THE FIX: Setting split fields into Entity
        driverEntity.setVehicleModel(vModel);
        driverEntity.setVehiclePlate(vPlate);
        
        driverEntity.setStatus(DriverStatus.OFFLINE); 

        // 6. Save Data
        Driver savedDriver = driverRepository.save(driverEntity);
        log.info("Driver registered successfully. ID: {}", savedDriver.getDriverId());
        
        return mapToDto(savedDriver);
    }

    @Override
    public DriverDto loginDriver(DriverLoginRequestDto loginRequest) {
        Driver driver = driverRepository.findByEmail(loginRequest.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new NoSuchElementException("Invalid Email or Password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), driver.getPassword())) {
            throw new IllegalArgumentException("Invalid Email or Password");
        }

        log.info("Driver authenticated: {}", loginRequest.getEmail());
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
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new NoSuchElementException("Driver not found with ID: " + driverId));

        try {
            DriverStatus newStatus = DriverStatus.valueOf(statusStr.toUpperCase());
            driver.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Use: AVAILABLE, BUSY, or OFFLINE");
        }

        return mapToDto(driverRepository.save(driver));
    }

    @Override
    public List<DriverDto> getAvailableDrivers() {
        List<Driver> drivers = driverRepository.findByStatus(DriverStatus.AVAILABLE);
        if (drivers.isEmpty()) {
            throw new NoSuchElementException("No available drivers found at the moment");
        }
        return drivers.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // 🚀 THE FIX: Helper method updated for split fields
    private DriverDto mapToDto(Driver driver) {
        return new DriverDto(
                driver.getDriverId(),
                driver.getName(),
                driver.getEmail(),
                driver.getPassword(),
                driver.getPhone(),
                driver.getLicenseNumber(),
                driver.getVehicleModel(), 
                driver.getVehiclePlate(),
                driver.getStatus()
        );
    }
}