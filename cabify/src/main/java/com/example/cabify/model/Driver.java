package com.example.cabify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverId;

    private String name;

    @Column(unique = true, nullable = false)
    private String email; // 🚀 Added for Login

    private String password; // 🚀 Added for Login

    private String phone;

    @Column(unique = true, nullable = false)
    private String licenseNumber;

    private String vehicleModel; 

    @Column(unique = true)
    private String vehiclePlate; 

    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.AVAILABLE; // Default status
}