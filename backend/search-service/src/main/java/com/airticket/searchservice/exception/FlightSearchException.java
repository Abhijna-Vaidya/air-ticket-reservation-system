package com.airticket.searchservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FlightSearchException extends RuntimeException {

    public FlightSearchException(String message) {
        super(message);
    }
}
