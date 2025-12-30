package com.airTecket.ticketCancellationService.service;

public interface EmailService {
	public void sendOtpEmail(String to, String otp);
	public void sendResponseEmail(String bookingId,String email);

}
