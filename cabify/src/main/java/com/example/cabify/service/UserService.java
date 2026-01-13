package com.example.cabify.service;

import com.example.cabify.dto.user.LoginRequestDto;
import com.example.cabify.dto.user.UserProfileDto;
import com.example.cabify.model.User;
import com.example.cabify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserProfileDto registerUser(User user) {
        // 1. Validation Logic
        log.info("Registering new user with email: {}", user.getEmail());
        if (user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Name, Email, and Password cannot be empty");
        }

        String name = user.getName().trim();
        String email = user.getEmail().trim();
        String phone = Long.toString(user.getPhone());
        String password = user.getPassword();

        if (name.length() < 3 || name.length() > 20) {
            throw new IllegalArgumentException("Username should be between 3 to 20 characters");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (phone.length() != 10) {
            throw new IllegalArgumentException("Provide a valid 10-digit phone number");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password length should be minimum of 8 characters");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email is already registered!");
        }

        // 2. Data Persistence
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setEmail(email);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getUserId());
        // 3. Return using helper method
        return mapToDto(savedUser);
    }

    public UserProfileDto getUserById(long id) {
        log.info("Fetching user details for ID: {}", id); // Log the fetch request
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new NoSuchElementException("User not found with ID: " + id);
                });
        return mapToDto(user);
    }

    public UserProfileDto userLogin(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return mapToDto(user);
    }

    public List<UserProfileDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new NoSuchElementException("No users found in the database");
        }
        // Using method reference for cleaner stream mapping
        return users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // --- Private Helper Method ---
    private UserProfileDto mapToDto(User user) {
        return new UserProfileDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }
}