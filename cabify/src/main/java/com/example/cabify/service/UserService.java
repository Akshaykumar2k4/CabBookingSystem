package com.example.cabify.service;

import com.example.cabify.dto.user.LoginRequestDto;
import com.example.cabify.dto.user.UserProfileDto;
import com.example.cabify.model.User;
import com.example.cabify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Change Importimport org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserProfileDto registerUser(User user) {

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

        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);
        user.setName(name);
        user.setEmail(email);
        User savedUser = userRepository.save(user);

        return new UserProfileDto(
                savedUser.getUserId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone()
        );

    }

    public UserProfileDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));

        return new UserProfileDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }

    public UserProfileDto userLogin(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

       if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
           throw new IllegalArgumentException("Invalid email or password");
       }

       return new UserProfileDto(
               user.getUserId(),
               user.getName(),
               user.getEmail(),
               user.getPhone()
       );
    }

    public List<UserProfileDto> getAllUsers() {
        List<User> users=userRepository.findAll();
        if (users.isEmpty()) {
            throw new NoSuchElementException("No users found in the database");
        }
        return users.stream()
                .map(user -> new UserProfileDto(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone()
                ))
                .collect(Collectors.toList());
    }
}
