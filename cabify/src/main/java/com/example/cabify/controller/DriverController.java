package com.example.cabify.controller;

import com.example.cabify.dto.SuccessResponse;
import com.example.cabify.dto.driver.DriverDto;
import com.example.cabify.model.Driver;
import com.example.cabify.service.IDriverService;
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

    // 1. Register a Driver (Matches UserController style)
    @PostMapping("register")
    public ResponseEntity<SuccessResponse<DriverDto>> registerDriver(@RequestBody Driver driver) {
        DriverDto registeredDriver = driverService.registerDriver(driver);

        SuccessResponse<DriverDto> response = new SuccessResponse<>(
                "Driver registered successfully!",
                HttpStatus.CREATED.value(),
                registeredDriver
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Get Available Drivers (Used by Customers)
    @GetMapping("available")
    public ResponseEntity<List<DriverDto>> getAvailableDrivers() {
        List<DriverDto> drivers = driverService.getAvailableDrivers();
        return new ResponseEntity<>(drivers, HttpStatus.OK);
    }

    // 3. Update Driver Status (e.g., PUT /api/drivers/status/1?status=AVAILABLE)
    @PutMapping("status/{id}")
    public ResponseEntity<DriverDto> updateDriverStatus(@PathVariable Long id, @RequestParam String status) {
        DriverDto updatedDriver = driverService.updateDriverStatus(id, status);
        return new ResponseEntity<>(updatedDriver, HttpStatus.OK);
    }

    // 4. Get Driver Profile
    @GetMapping("{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable Long id) {
        DriverDto driver = driverService.getDriverById(id);
        return new ResponseEntity<>(driver, HttpStatus.OK);
    }
}