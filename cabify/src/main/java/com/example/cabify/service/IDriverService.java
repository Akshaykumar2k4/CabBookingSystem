package com.example.cabify.service;

import com.example.cabify.dto.driver.DriverDto;
import com.example.cabify.model.Driver;
import java.util.List;

public interface IDriverService {

    // Return DTO instead of Entity
    DriverDto registerDriver(Driver driver);

    DriverDto getDriverById(Long driverId);

    DriverDto updateDriverStatus(Long driverId, String status);

    List<DriverDto> getAvailableDrivers();
}