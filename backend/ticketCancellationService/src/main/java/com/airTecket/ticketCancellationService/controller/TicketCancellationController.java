package com.airTecket.ticketCancellationService.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.airTecket.ticketCancellationService.dto.CancelResponseDTO;
import com.airTecket.ticketCancellationService.dto.CustomerAuthResponseDTO;
import com.airTecket.ticketCancellationService.exception.CustomerAuthException;
import com.airTecket.ticketCancellationService.feign.CustomerClient;
import com.airTecket.ticketCancellationService.service.OtpService;
import com.airTecket.ticketCancellationService.service.TicketCancellationService;

@RestController
@RequestMapping("/api/ticketCancel/otp")
public class TicketCancellationController {

	@Autowired
	private OtpService otpService;

	@Autowired
	private CustomerClient customerClient;



	@Autowired
	private TicketCancellationService ticketCancellationService;

	// Step 1: Generate & Send OTP
	@GetMapping("/request")
	public ResponseEntity<String> requestOtp(@RequestHeader("Authorization") String token) {
		CustomerAuthResponseDTO customerAuthResponse = authenticateAndGetCustomer(token);

		String email = customerAuthResponse.getEmail();
		otpService.generateAndSendOtp(email);

		return ResponseEntity.ok("OTP sent to email: " + email);
	}

	// Step 2: Verify OTP
	@PostMapping("/{bookingId}/verify")
	public ResponseEntity<?> verifyOtp(
			@RequestHeader("Authorization") String token,
			@RequestParam("cancellationDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cancellationDate,
			@PathVariable("bookingId") String bookingId,
			@RequestBody Map<String, String> body) {

		String otp = body.get("otp"); // extract OTP
		CustomerAuthResponseDTO customerAuthResponse = authenticateAndGetCustomer(token);
		String email = customerAuthResponse.getEmail();

		boolean isValid = otpService.verifyOtp(email, otp);

		if (isValid) {
			CancelResponseDTO response =  ticketCancellationService.cancelTicket(bookingId,cancellationDate, customerAuthResponse);
			return ResponseEntity.ok(response);

		} else {
			return ResponseEntity.badRequest().body("Invalid or expired OTP");
		}
	}

	@DeleteMapping("/{bookingId}")
	public ResponseEntity<?> cancelTicket(
			@RequestHeader(value = "Authorization") String token,
			@PathVariable("bookingId") String bookingId,
			@RequestParam("cancellationDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cancellationDate) {

		// 1. Check token existence
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Unauthorized: Missing or invalid token");
		}

		// 2. Validate token via registration service
		CustomerAuthResponseDTO customerAuthResponse = customerClient.validateToken(token);
		//logger.info("CustomerAuthResponseDTO received: {}", customerAuthResponse);
		if (!customerAuthResponse.isValid()|| !customerAuthResponse.getRole().equals("ADMIN")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Access Declined");
		}

		Integer customerId = customerAuthResponse.getCustomerId();

		// 3. Proceed with cancellation
		CancelResponseDTO response = ticketCancellationService.cancelTicket(bookingId, cancellationDate, customerAuthResponse);
		return ResponseEntity.ok(response);
	}

	//Helper function to validate the token
	private CustomerAuthResponseDTO authenticateAndGetCustomer(String token) {
		if (token == null || !token.startsWith("Bearer ")) {
			throw new RuntimeException("Unauthorized: Missing or invalid token");
		}

		CustomerAuthResponseDTO customerAuthResponse = customerClient.validateToken(token);
		if (!customerAuthResponse.isValid()) {
			throw new CustomerAuthException("Access Declined");
		}

		return customerAuthResponse;
	}

}
