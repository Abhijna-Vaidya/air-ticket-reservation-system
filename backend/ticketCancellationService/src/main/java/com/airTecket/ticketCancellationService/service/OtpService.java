package com.airTecket.ticketCancellationService.service;


public interface OtpService {
	public void generateAndSendOtp(String email) ;
	public boolean verifyOtp(String email, String otp);

}
