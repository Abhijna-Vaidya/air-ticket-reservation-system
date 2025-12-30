package com.airTecket.ticketCancellationService.service.serviceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airTecket.ticketCancellationService.service.EmailService;
import com.airTecket.ticketCancellationService.service.OtpService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService{

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    private final SecureRandom random = new SecureRandom();
    private final Map<String, OtpDetails> otpStore = new ConcurrentHashMap<>();
    
    @Autowired
    private EmailService emailService;
    
    public void generateAndSendOtp(String email) {
        String otp = String.format("%06d", random.nextInt(999999));

        otpStore.put(email, new OtpDetails(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)));

        // Send OTP to customer's email
        emailService.sendOtpEmail(email, otp);

        System.out.println("Generated OTP for " + email + ": " + otp); // Debug
    }

    public boolean verifyOtp(String email, String otp) {
        OtpDetails details = otpStore.get(email);

        if (details == null || LocalDateTime.now().isAfter(details.expiryTime())) {
            otpStore.remove(email);
            return false;
        }

        boolean isValid = details.otp().equals(otp);

        if (isValid) {
            otpStore.remove(email);
        }

        return isValid;
    }

    private record OtpDetails(String otp, LocalDateTime expiryTime) {}
}
