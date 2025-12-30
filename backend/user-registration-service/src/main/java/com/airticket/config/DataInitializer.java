package com.airticket.config;

import com.airticket.model.User;
import com.airticket.model.Role;
import com.airticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // this creates the default admin
        if (userRepository.findByEmail("admin@airline.com").isEmpty()) {
            User admin = User.builder()
                .firstName("Admin")
                .lastName("User")
                .gender("Other")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("admin@airline.com")
                .password(passwordEncoder.encode("admin@123"))
                .phoneNumber("1234567890")
                .address("Admin Address")
                .role(Role.ADMIN)
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            userRepository.save(admin);
            System.out.println("Admin user created: admin@airline.com / admin@123");
        }
    }
}