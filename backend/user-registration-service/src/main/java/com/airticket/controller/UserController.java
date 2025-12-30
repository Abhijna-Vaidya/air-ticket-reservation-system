package com.airticket.controller;

import com.airticket.service.UserService;
import com.airticket.util.JwtUtil;
import com.airticket.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth/customers")
//@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    

     private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
     @Autowired
    private JwtUtil jwtUtil;
    
    
    
 // user registration for customer only
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO requestDto) {
        UserResponseDTO responseDto = userService.registerUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    
   // ------------------------------------------ Login related apis----------------------------------------
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequest) {
        AuthResponseDTO response = userService.authenticateUser(authRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : 
               ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserResponseDTO responseDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(responseDto);
    }
    
    @PutMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateProfile(@Valid @RequestBody UserRequestDTO requestDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserResponseDTO user = userService.getUserByEmail(email);
        UserResponseDTO responseDto = userService.updateUser(user.getUserId(), requestDto);
        return ResponseEntity.ok(responseDto);
    }
    
    @DeleteMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponseDTO> deleteProfile(@RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserResponseDTO user = userService.getUserByEmail(email);
        String password = request.get("password");
        ApiResponseDTO response = userService.deleteUser(user.getUserId(), password);
        
        return response.isSuccess() ? ResponseEntity.ok(response) : 
               ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
   
    //-------------------------------------------forget,change password & reset password apis-------------------------
    @PostMapping("/forgotpassword")
    public ResponseEntity<ApiResponseDTO> forgotPassword(@Valid @RequestBody EmailActionDTO emailRequest) {
        ApiResponseDTO response = userService.generateResetToken(emailRequest.getEmail());
        return response.isSuccess() ? ResponseEntity.ok(response) : 
               ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @PostMapping("/resetpassword")
    public ResponseEntity<ApiResponseDTO> resetPassword(@Valid @RequestBody ResetPasswordDTO resetRequest) {
        ApiResponseDTO response = userService.resetPassword(resetRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : 
               ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
      @PutMapping("/change-password")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserResponseDTO user = userService.getUserByEmail(email);
        ApiResponseDTO response = userService.changePassword(user.getUserId(), changePasswordRequest);
        
        return response.isSuccess() ? ResponseEntity.ok(response) : 
               ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //---------------------------------Email verficiation-------------------------------------
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponseDTO> verifyEmail(@Valid @RequestBody EmailActionDTO verifyRequest) {
        ApiResponseDTO response = userService.verifyEmail(verifyRequest);
        return response.isSuccess() ? ResponseEntity.ok(response) : 
               ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
  
    



    //-------------------------------------------------Jwt token validation api---------------------------------
    // use this api to validate the token in you service
     @GetMapping("/validate")
    public ResponseEntity<CustomerAuthResponseDTO> validateToken(@RequestHeader(value="Authorization", required=false) String authHeader) {

       CustomerAuthResponseDTO customerAuthResponseDTO = new CustomerAuthResponseDTO();
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            customerAuthResponseDTO.setValid(false);
            return ResponseEntity.ok(customerAuthResponseDTO);
        }
        String token = authHeader.substring(7).trim();
        logger.info("The required token is : {}", token);

        if (jwtUtil.isTokenValid(token)) {
            String email = jwtUtil.getEmailFromToken(token);
            Integer customerId = jwtUtil.getCustomerIdFromToken(token);
            UserResponseDTO customer = userService.getUserByEmail(email);

            customerAuthResponseDTO.setValid(true);
            customerAuthResponseDTO.setCustomerId(customerId);
            customerAuthResponseDTO.setRole(customer.getRole());
            customerAuthResponseDTO.setEmail(customer.getEmail());
            logger.info("Response created: {}", customerAuthResponseDTO);
            return ResponseEntity.ok(customerAuthResponseDTO);
        } else {
            customerAuthResponseDTO.setValid(false);
            customerAuthResponseDTO.setRole("Anonymous");
            return ResponseEntity.ok(customerAuthResponseDTO);
        }
    }
}