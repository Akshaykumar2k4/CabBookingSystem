package com.example.cabify.service;

import com.example.cabify.dto.payment.PaymentRequestDto;
import com.example.cabify.dto.payment.PaymentResponseDto;

public interface IPaymentService {
    PaymentResponseDto processPayment(PaymentRequestDto request);
    PaymentResponseDto getReceipt(Long rideId);
}