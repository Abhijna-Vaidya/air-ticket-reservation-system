package com.airTecket.ticketCancellationService.service.serviceImpl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.airTecket.ticketCancellationService.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService{

	@Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Ticket Cancellation OTP");
        message.setText("Dear Customer,\n\nYour OTP for ticket cancellation is: " + otp + "\n\nThis OTP will expire in 5 minutes.\n\nThank you.");
        mailSender.send(message);
    }
    
    public void sendResponseEmail(String bookingId,String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Ticket Cancellation Confirmation mail");
        message.setText("Dear Customer,\n\nYour ticket with booking ID "+bookingId+" has been cancelled successfully.\n\nThank you.");
        mailSender.send(message);
    }
}
