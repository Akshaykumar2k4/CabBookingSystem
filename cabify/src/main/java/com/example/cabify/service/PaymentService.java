package com.example.cabify.service;

import com.example.cabify.dto.payment.PaymentRequestDto;
import com.example.cabify.dto.payment.PaymentResponseDto;
import com.example.cabify.exception.ResourceNotFoundException;
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
        //  Validate Ride
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + request.getRideId()));

        //  Validate User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        //  Check if already paid (Prevent Double Payment)
        // If this throws, GlobalExceptionHandler returns 409 Conflict
        if (ride.getUser().getUserId() != user.getUserId()) {
            throw new SecurityException("Unauthorized: You cannot pay for another user's ride.");
        }
        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new IllegalStateException("Cannot pay. Ride status is " + ride.getStatus() + ", expected COMPLETED.");
        }
        if (paymentRepository.findByRide(ride).isPresent()) {
            throw new IllegalStateException("Payment already made for this ride.");
        }

        // Simulate Payment Gateway (As per requirement)
        boolean bankSuccess = simulateBankTransaction();
        PaymentStatus status = bankSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        if (!bankSuccess) {
            // If this throws, GlobalExceptionHandler returns 500 Internal Server Error
            throw new RuntimeException("Payment Gateway Failed");
        }

        //  Save Payment
        Payment payment = new Payment();
        payment.setRide(ride);
        payment.setUser(user);
        payment.setAmount(ride.getFare());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(status);
        // Timestamp is handled automatically by @PrePersist in Entity

        paymentRepository.save(payment);
        ride.setStatus(RideStatus.PAID);
        rideRepository.save(ride);

        //  Return Receipt
        return mapToDto(payment);
    }

    // Logic for: GET /api/payments/receipt/{rideId}
    public PaymentResponseDto getReceipt(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + rideId));

        Payment payment = paymentRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found for ride ID: " + rideId));

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