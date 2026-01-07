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

    private String phone;

    @Column(unique = true, nullable = false)
    private String licenseNumber;

    private String vehicleDetails; // e.g., "Toyota Prius - KA05MX1234"

    @Enumerated(EnumType.STRING)
    private DriverStatus status;
}