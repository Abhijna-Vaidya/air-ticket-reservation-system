package com.airticket.flight_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FlightAlreadyExistsException  extends Exception {

    public FlightAlreadyExistsException (String msg){
        super(msg);

    }
}
