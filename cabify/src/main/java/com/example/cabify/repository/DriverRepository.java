package com.example.cabify.repository;

import com.example.cabify.model.Driver;
import com.example.cabify.model.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Custom method to find drivers by their status (e.g., fetch only "AVAILABLE" ones)
    List<Driver> findByStatus(DriverStatus status);

    // Optional: Check if a driver exists by license number (to prevent duplicates)
    boolean existsByLicenseNumber(String licenseNumber);
}