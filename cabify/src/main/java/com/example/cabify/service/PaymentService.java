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
public class PaymentService implements IPaymentService { // <--- Implements Interface

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Override // <--- Added Override annotation
    @Transactional
    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        // Logic remains exactly the same
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + request.getRideId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        if (!ride.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("Unauthorized: You cannot pay for another user's ride.");
        }
        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new IllegalStateException("Cannot pay. Ride status is " + ride.getStatus() + ", expected COMPLETED.");
        }
        if (paymentRepository.findByRide(ride).isPresent()) {
            throw new IllegalStateException("Payment already made for this ride.");
        }

        boolean bankSuccess = simulateBankTransaction();
        PaymentStatus status = bankSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        if (!bankSuccess) {
            throw new RuntimeException("Payment Gateway Failed");
        }

        Payment payment = new Payment();
        payment.setRide(ride);
        payment.setUser(user);
        payment.setAmount(ride.getFare());
        payment.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        payment.setStatus(status);

        paymentRepository.save(payment);
        ride.setStatus(RideStatus.PAID);
        rideRepository.save(ride);

        return mapToDto(payment);
    }

    @Override // <--- Added Override annotation
    public PaymentResponseDto getReceipt(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + rideId));

        Payment payment = paymentRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found for ride ID: " + rideId));

        return mapToDto(payment);
    }

    private boolean simulateBankTransaction() {
        return true;
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