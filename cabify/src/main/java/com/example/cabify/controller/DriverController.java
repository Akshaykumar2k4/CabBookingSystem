package com.example.cabify.controller;

import com.example.cabify.dto.SuccessResponse;
import com.example.cabify.dto.driver.DriverDto;
import com.example.cabify.dto.driver.DriverLoginRequestDto;
import com.example.cabify.dto.driver.DriverLoginResponseDto;
import com.example.cabify.model.Driver;
import com.example.cabify.service.IDriverService;
import com.example.cabify.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers/")
public class DriverController {

    @Autowired
    private IDriverService driverService;

    @Autowired
    private JwtUtil jwtUtil;
    // 1. Register a Driver
    @PostMapping("register")
    public ResponseEntity<SuccessResponse<DriverDto>> registerDriver(@Valid @RequestBody DriverDto driverDto) {
        DriverDto registeredDriver = driverService.registerDriver(driverDto);

        SuccessResponse<DriverDto> response = new SuccessResponse<>(
                "Driver registered successfully!",
                HttpStatus.CREATED.value(),
                registeredDriver
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Get Available Drivers (UPDATED)
    @GetMapping("available")
    public ResponseEntity<SuccessResponse<List<DriverDto>>> getAvailableDrivers() {
        List<DriverDto> drivers = driverService.getAvailableDrivers();

        SuccessResponse<List<DriverDto>> response = new SuccessResponse<>(
                "Available drivers fetched successfully", // Descriptive message
                HttpStatus.OK.value(),
                drivers
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 3. Update Driver Status (UPDATED)
    @PutMapping("status/{id}")
    public ResponseEntity<SuccessResponse<DriverDto>> updateDriverStatus(
            @PathVariable Long id,
            @RequestParam("status") String status) {

        // 🚀 This updates the driver status in the database
        DriverDto updatedDriver = driverService.updateDriverStatus(id, status);

        return new ResponseEntity<>(new SuccessResponse<>(
                "Status updated to " + status,
                HttpStatus.OK.value(),
                updatedDriver
        ), HttpStatus.OK);
    }

    // 4. Get Driver Profile (UPDATED)
    @GetMapping("{id}")
    public ResponseEntity<SuccessResponse<DriverDto>> getDriverById(@PathVariable Long id) {
        DriverDto driver = driverService.getDriverById(id);

        SuccessResponse<DriverDto> response = new SuccessResponse<>(
                "Driver details fetched successfully",
                HttpStatus.OK.value(),
                driver
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // Add this to your DriverController.java
    @PostMapping("login")
    public ResponseEntity<SuccessResponse<DriverLoginResponseDto>> loginDriver(
            @Valid @RequestBody DriverLoginRequestDto loginRequest) {

        // ❌ WRONG: driverService.loginDriver(loginRequest.getEmail());
        // ✅ CORRECT: Pass the whole object
        DriverDto driverDto = driverService.loginDriver(loginRequest);

        String token = jwtUtil.generateToken(driverDto.getEmail());

        return new ResponseEntity<>(new SuccessResponse<>(
                "Login successful!",
                HttpStatus.OK.value(),
                new DriverLoginResponseDto(token, driverDto)),
                HttpStatus.OK);
    }
}