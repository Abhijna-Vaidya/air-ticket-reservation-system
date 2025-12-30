package com.airTecket.ticketCancellationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "flight-service")
public interface FlightClient {
    @PutMapping("/api/flights/{flightId}/seats")
    public ResponseEntity<String> updateTicketsAndSeatAvailability(@PathVariable("flightId") Integer flightId, @RequestParam("bookingId") String bookingId, @RequestParam("seats") Integer seats);
}
