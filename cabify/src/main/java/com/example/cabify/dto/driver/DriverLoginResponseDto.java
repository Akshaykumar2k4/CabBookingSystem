package com.example.cabify.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverLoginResponseDto {
    private String token; 
    private DriverDto driver;
}