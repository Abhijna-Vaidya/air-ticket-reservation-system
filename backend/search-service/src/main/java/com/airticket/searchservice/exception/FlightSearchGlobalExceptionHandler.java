package com.airticket.searchservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.util.stream.Collectors;

@RestControllerAdvice
public class FlightSearchGlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(FlightNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundException(FlightNotFoundException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDate.now(),
                ex.getMessage(),
                request.getDescription(false),
                "Not Found"
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FlightAlreadyExistsException.class)
    public final ResponseEntity<ExceptionResponse> handleFlightAlreadyExistsException(
            FlightAlreadyExistsException ex, WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                LocalDate.now(),
                ex.getMessage(),
                request.getDescription(false),
                "Conflict"
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDate.now(),
                ex.getMessage(),
                request.getDescription(false),
                "Internal Server Error"
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ExceptionResponse response = new ExceptionResponse(
                LocalDate.now(),
                "Validation Failed",
                validationErrors,
                "Bad Request"
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
