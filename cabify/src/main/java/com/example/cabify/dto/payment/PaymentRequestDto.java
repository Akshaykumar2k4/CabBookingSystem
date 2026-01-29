package com.example.cabify.dto.payment;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private Long rideId;
    private Long userId;
    private String paymentMethod;
}