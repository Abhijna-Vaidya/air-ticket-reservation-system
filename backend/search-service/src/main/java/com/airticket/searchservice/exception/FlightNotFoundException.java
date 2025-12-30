package com.airticket.searchservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FlightNotFoundException extends Exception  {

    public FlightNotFoundException (String msg){
        super(msg);
    }

}
