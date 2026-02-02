package com.example.cabify.service;

import com.example.cabify.dto.user.LoginRequestDto;
import com.example.cabify.dto.user.UserProfileDto;
import com.example.cabify.model.User;
import java.util.List;

public interface IUserService {
    UserProfileDto registerUser(User user);
    UserProfileDto getUserById(long id);
    UserProfileDto userLogin(LoginRequestDto loginRequestDto);
    List<UserProfileDto> getAllUsers();
}