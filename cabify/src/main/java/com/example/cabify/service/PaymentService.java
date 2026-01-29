package com.example.cabify.service;

import com.example.cabify.dto.payment.PaymentRequestDto;
import com.example.cabify.dto.payment.PaymentResponseDto;
import com.example.cabify.model.*;
import com.example.cabify.repository.PaymentRepository;
import com.example.cabify.repository.RideRepository;
import com.example.cabify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    // Logic for: POST /api/payments/process
    @Transactional
    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        // Validate Ride
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Validate User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if already paid (Prevent Double Payment)
        if (paymentRepository.findByRide(ride).isPresent()) {
            throw new IllegalStateException("Payment already made for this ride.");
        }

        boolean bankSuccess = simulateBankTransaction();
        PaymentStatus status = bankSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        if (!bankSuccess) {
            throw new RuntimeException("Payment Gateway Failed");
        }

        //  Save Payment
        Payment payment = new Payment();
        payment.setRide(ride);
        payment.setUser(user);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(status);
        // Timestamp is handled automatically by @PrePersist

        paymentRepository.save(payment);

        //Return Receipt
        return mapToDto(payment);
    }

    // Logic for: GET /api/payments/receipt/{rideId}
    public PaymentResponseDto getReceipt(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        Payment payment = paymentRepository.findByRide(ride)
                .orElseThrow(() -> new RuntimeException("Receipt not found for this ride"));

        return mapToDto(payment);
    }

    // Helper: Simulate 3rd Party Gateway
    private boolean simulateBankTransaction() {
        return true; // Always return true for development
    }

    private PaymentResponseDto mapToDto(Payment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setRideId(payment.getRide().getId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setTimestamp(payment.getTimestamp());
        return dto;
    }
}