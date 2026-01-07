package com.example.cabify.controller;

import com.example.cabify.dto.SuccessResponse;
import com.example.cabify.dto.user.AuthResponseDto;
import com.example.cabify.dto.user.LoginRequestDto;
import com.example.cabify.dto.user.UserProfileDto;
import com.example.cabify.model.User;
import com.example.cabify.service.UserService;
import com.example.cabify.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("register")
        public ResponseEntity<SuccessResponse<UserProfileDto>> registerUser(@RequestBody User user) {
        UserProfileDto userProfile = userService.registerUser(user);
        SuccessResponse<UserProfileDto> response = new SuccessResponse<>(
                "User successfully created!",
                HttpStatus.CREATED.value(),
                userProfile
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<?> userLogin(@RequestBody LoginRequestDto loginRequestDto) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect email or password", e);
        }

        final String jwt = jwtUtil.generateToken(loginRequestDto.getEmail());
        return ResponseEntity.ok(new AuthResponseDto(jwt));
    }

    @GetMapping("profile/{id}")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable int id) {
        UserProfileDto profile = userService.getUserById(id);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping("profile")
    public ResponseEntity<List<UserProfileDto>> getAllUsers(){
        List<UserProfileDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}