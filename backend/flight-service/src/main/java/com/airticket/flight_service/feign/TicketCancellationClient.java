package com.airticket.flight_service.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "ticket-cancellation-service")
public interface TicketCancellationClient {
    @DeleteMapping("/api/ticketCancel/otp/{bookingId}")
    public ResponseEntity<?> cancelTicket(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable String bookingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cancellationDate);
}
