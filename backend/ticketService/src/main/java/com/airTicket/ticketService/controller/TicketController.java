package com.airTicket.ticketService.controller;


import com.airTicket.ticketService.dto.CustomerAuthResponseDTO;
import com.airTicket.ticketService.dto.TicketDTO;
import com.airTicket.ticketService.feign.CustomerClient;
import com.airTicket.ticketService.service.TicketBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.airTicket.ticketService.dto.BookingRequestDTO;
import com.airTicket.ticketService.dto.BookingResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    @Autowired
    private CustomerClient customerClient;
    @Autowired
    private TicketBookingService ticketBookingService;

    // BOOK
    @PostMapping("/book")
    public ResponseEntity<?> bookTicket(@RequestHeader(value = "Authorization") String token, @RequestBody BookingRequestDTO request) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Missing or invalid token");
        }
        CustomerAuthResponseDTO customerAuthResponse = customerClient.validateToken(token);
        if (!customerAuthResponse.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Access Declined");
        }
        logger.info("Response created: {}", customerAuthResponse);
        request.setCustomerId(customerAuthResponse.getCustomerId());
        BookingResponseDTO response = ticketBookingService.bookTicket(request,customerAuthResponse);
        return ResponseEntity.ok(response);

    }
    @GetMapping("/{bookingId}")
    public ResponseEntity<TicketDTO> getTicketByBookingId(@PathVariable("bookingId") String bookingId) {
       TicketDTO returnedTicket= ticketBookingService.getTicketByBookingId(bookingId);
       return new ResponseEntity<>(returnedTicket,HttpStatus.OK);
    }

    @PutMapping("{bookingId}/status")
    public ResponseEntity<String> updateTicketStatus(@PathVariable("bookingId") String bookingId, @RequestParam("status") String status)
    {
        String result = ticketBookingService.updateTicketStatus(bookingId,status);

            return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("customer/myTickets")
    public ResponseEntity<?> getMyTickets(
            @RequestHeader(value = "Authorization") String authHeader) {
        CustomerAuthResponseDTO customerAuthResponse = customerClient.validateToken(authHeader);

        if (!customerAuthResponse.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Declined");
        }
        return ResponseEntity.ok(ticketBookingService.getTicketsByCustomerId(customerAuthResponse.getCustomerId()));
    }

}
