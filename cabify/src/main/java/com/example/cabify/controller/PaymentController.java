package com.example.cabify.controller;

import com.example.cabify.dto.SuccessResponse;
import com.example.cabify.dto.payment.PaymentRequestDto;
import com.example.cabify.dto.payment.PaymentResponseDto;
import com.example.cabify.service.IPaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    // 1. Process a Payment
    @PostMapping("/process")
    public ResponseEntity<SuccessResponse<PaymentResponseDto>> processPayment(@Valid @RequestBody PaymentRequestDto request) {
        PaymentResponseDto result = paymentService.processPayment(request);

        SuccessResponse<PaymentResponseDto> response = new SuccessResponse<>(
                "Payment processed successfully",
                HttpStatus.CREATED.value(),
                result
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Get Payment Receipt
    @GetMapping("/receipt/{rideId}")
    public ResponseEntity<SuccessResponse<PaymentResponseDto>> getReceipt(@PathVariable Long rideId) {
        PaymentResponseDto result = paymentService.getReceipt(rideId);

        SuccessResponse<PaymentResponseDto> response = new SuccessResponse<>(
                "Receipt fetched successfully",
                HttpStatus.OK.value(),
                result
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}