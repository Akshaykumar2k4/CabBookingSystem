package com.example.cabify.service;

import com.example.cabify.dto.driver.DriverDto;
import com.example.cabify.dto.driver.DriverLoginRequestDto;
import com.example.cabify.model.Driver;

import jakarta.validation.Valid;

import java.util.List;

public interface IDriverService {

    // Return DTO instead of Entity
    DriverDto registerDriver(DriverDto driverDto);

    DriverDto getDriverById(Long driverId);

    DriverDto updateDriverStatus(Long driverId, String status);

    List<DriverDto> getAvailableDrivers();

    DriverDto loginDriver(DriverLoginRequestDto loginRequest);}