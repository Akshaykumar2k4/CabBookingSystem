package com.example.cabify.dto.driver;

import com.example.cabify.model.DriverStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDto {

    private Long driverId; 

    @NotBlank(message = "Driver name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email; 

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password; 

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "License number is required")
    @Size(min = 5, message = "License number is too short")
    private String licenseNumber;

    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel; 

    @NotBlank(message = "Vehicle plate is required")
    private String vehiclePlate;

    private DriverStatus status;
}