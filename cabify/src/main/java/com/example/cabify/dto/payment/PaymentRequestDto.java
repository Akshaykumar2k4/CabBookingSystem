package com.example.cabify.dto.payment;

import com.example.cabify.model.PaymentMethod;
import lombok.Data;

@Data
public class PaymentRequestDto {
    private Long rideId;
    private Long userId;
    private PaymentMethod paymentMethod;
}