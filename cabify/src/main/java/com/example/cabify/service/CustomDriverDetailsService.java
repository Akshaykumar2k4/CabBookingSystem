package com.example.cabify.service;

import com.example.cabify.model.Driver;
import com.example.cabify.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomDriverDetailsService implements UserDetailsService {

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Look specifically in the Drivers table
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Driver not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                driver.getEmail(),
                driver.getPassword(),
                new ArrayList<>() // You can add ROLE_DRIVER here later
        );
    }
}