package com.example.cabify.repository;

import com.example.cabify.model.Driver;
import com.example.cabify.model.DriverStatus;
import com.example.cabify.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Custom method to find drivers by their status (e.g., fetch only "AVAILABLE" ones)
    List<Driver> findByStatus(DriverStatus status);

    // Optional: Check if a driver exists by license number (to prevent duplicates)
    boolean existsByLicenseNumber(String licenseNumber);

    Optional<Driver> findFirstByStatus(DriverStatus available);
}