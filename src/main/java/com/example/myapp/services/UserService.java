package com.example.myapp.services;

import com.example.myapp.entities.User;
import com.example.myapp.enums.Role;
import com.example.myapp.payload.request.LoginRequest;
import com.example.myapp.payload.request.SignupRequest;

import java.util.List;
import java.util.Optional;


public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    List<User> getUsersByRole(Role role);
    User registerUser(SignupRequest signupRequest);
    User authenticateUser(LoginRequest loginRequest);
    boolean existsByEmail(String email);
    User saveUser(User user);
    void deleteUser(Long id);
}

