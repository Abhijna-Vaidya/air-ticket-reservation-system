package com.airTecket.ticketCancellationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.airTecket.ticketCancellationService.dto.TicketDTO;

@FeignClient(name = "ticketService")
public interface BookingServiceClient {
	    @GetMapping("/api/tickets/{bookingId}")
	    TicketDTO getTicketByBookingId(@PathVariable String bookingId);

	    @PutMapping("/api/tickets/{bookingId}/status")
		ResponseEntity<String> updateTicketStatus(@PathVariable String bookingId, @RequestParam String status);
	


}
