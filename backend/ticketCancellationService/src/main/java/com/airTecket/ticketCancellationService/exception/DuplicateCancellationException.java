package com.airTecket.ticketCancellationService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONFLICT)
public class DuplicateCancellationException extends RuntimeException {
    public DuplicateCancellationException(String message) {
        super(message);
    }
}