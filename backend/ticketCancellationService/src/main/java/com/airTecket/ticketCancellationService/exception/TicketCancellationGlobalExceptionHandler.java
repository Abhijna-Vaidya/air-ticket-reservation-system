package com.airTecket.ticketCancellationService.exception;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class TicketCancellationGlobalExceptionHandler extends ResponseEntityExceptionHandler {

// handles custom exception

	// Handles all general exceptions
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), ex.getMessage(),
				request.getDescription(false), "Internal Server Error");
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Handles TicketNotFoundException
	@ExceptionHandler(TicketNotFoundException.class)
	public final ResponseEntity<ExceptionResponse> handleTicketNotFound(TicketNotFoundException ex,
			WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), ex.getMessage(),
				request.getDescription(false), "Not Found");
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	// Handles CustomerAuthException
	@ExceptionHandler(CustomerAuthException.class)
	public final ResponseEntity<ExceptionResponse> handleCustomerAuthException(CustomerAuthException ex,
																			   WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), ex.getMessage(),
				request.getDescription(false), "FORBIDDEN");
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	// HAndles DuplicateCancellationException
	@ExceptionHandler(DuplicateCancellationException.class)
	public final ResponseEntity<ExceptionResponse> handleDuplicateCancellation(DuplicateCancellationException ex,
			WebRequest request) {
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), ex.getMessage(),
				request.getDescription(false), "Conflict");
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}
	
	// Handles MailSendException
	@ExceptionHandler(MailSendException.class)
    public ResponseEntity<ExceptionResponse> handleMailSendException(MailSendException ex,WebRequest request) {
		String message="Something went wrong while sending the email. Please verify your email address or try again in a few minutes.";
		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), message,
				request.getDescription(false), "Internal Server Error");
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

	// Handles validation errors (like @NotEmpty, @Min, etc.)
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String validationErrors = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining(", "));

		ExceptionResponse response = new ExceptionResponse(LocalDate.now(), "Validation Failed", validationErrors,
				"Bad Request");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
