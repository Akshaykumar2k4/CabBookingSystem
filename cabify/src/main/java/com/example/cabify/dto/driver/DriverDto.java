package com.example.cabify.dto.driver;

import com.example.cabify.model.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDto {
    private Long driverId;
    private String name;
    private String phone;
    private String licenseNumber;
    private String vehicleDetails;
    private DriverStatus status;
}