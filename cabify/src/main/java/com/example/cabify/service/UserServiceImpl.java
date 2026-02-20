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
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserProfileDto registerUser(User user) {
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
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format. Domain must include a dot (e.g., .com)");
        }
        if (phone.length() != 10) {
            throw new IllegalArgumentException("Provide a valid 10-digit phone number");
        }
        if (password.length() < 8 || 
            !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email is already registered!");
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalStateException("This phone number is already registered!");
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setEmail(email);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getUserId());
        return mapToDto(savedUser);
    }

    @Override
    public UserProfileDto getUserById(long id) {
        log.info("Fetching user details for ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new NoSuchElementException("User not found with ID: " + id);
                });
        return mapToDto(user);
    }

    @Override
    public UserProfileDto userLogin(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return mapToDto(user);
    }

    @Override
    public List<UserProfileDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new NoSuchElementException("No users found in the database");
        }
        return users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private UserProfileDto mapToDto(User user) {
        return new UserProfileDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }
}