package com.example.cabify.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PaymentRequestDto {

    @NotNull(message = "Ride ID is required")
    private Long rideId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Payment method is required")
    
    @Pattern(regexp = "^(CASH|CARD|UPI)$", message = "Invalid payment method")
    private String paymentMethod;
}