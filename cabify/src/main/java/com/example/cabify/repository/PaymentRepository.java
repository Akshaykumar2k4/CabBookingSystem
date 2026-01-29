package com.example.cabify.repository;

import com.example.cabify.model.Payment;
import com.example.cabify.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Custom query method: "SELECT * FROM payments WHERE ride_id = ?"
    // We need this to ensure a ride isn't paid for twice, and to fetch the receipt.
    Optional<Payment> findByRide(Ride ride);
}