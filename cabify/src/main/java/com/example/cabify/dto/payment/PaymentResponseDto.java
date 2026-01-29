package com.example.cabify.dto.payment;

import com.example.cabify.model.PaymentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDto {
    private Long paymentId;
    private Long rideId;
    private PaymentStatus status;
    private Double amount;
    private LocalDateTime timestamp;
}