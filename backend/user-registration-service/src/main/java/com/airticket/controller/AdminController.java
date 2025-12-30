package com.airticket.controller;

import com.airticket.service.UserService;
import com.airticket.dto.*;
import com.airticket.model.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDto) {
        UserResponseDTO responseDto = userService.registerUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/customers")
    public ResponseEntity<List<UserResponseDTO>> getAllCustomers() {
        List<UserResponseDTO> customers = userService.getUsersByRole(Role.CUSTOMER);
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/users/admins")
    public ResponseEntity<List<UserResponseDTO>> getAllAdmins() {
        List<UserResponseDTO> admins = userService.getUsersByRole(Role.ADMIN);
        return ResponseEntity.ok(admins);
    }
    
    @PostMapping("/users/get")
    public ResponseEntity<UserResponseDTO> getUserById(@Valid @RequestBody AdminRequestDTO request) {
        UserResponseDTO user = userService.getUserByEmail(request.getEmail());
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/users/update")
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UserRequestDTO requestDto) {
        UserResponseDTO responseDto = userService.updateUser(requestDto.getUserId(), requestDto);
        return ResponseEntity.ok(responseDto);
    }
    
    @PutMapping("/users/role")
    public ResponseEntity<ApiResponseDTO> updateUserRole(@Valid @RequestBody AdminRequestDTO request) {
        try {
            Role role = Role.valueOf(request.getRole().toUpperCase());
            userService.updateUserRole(request.getUserId(), role);
            return ResponseEntity.ok(new ApiResponseDTO(true, "User role updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponseDTO(false, "Invalid role: " + request.getRole()));
        }
    }
    
    @DeleteMapping("/users/delete")
    public ResponseEntity<ApiResponseDTO> deleteUser(@Valid @RequestBody AdminRequestDTO request) {
        userService.deleteUserByAdmin(request.getUserId());
        return ResponseEntity.ok(new ApiResponseDTO(true, "User deleted successfully"));
    }
}