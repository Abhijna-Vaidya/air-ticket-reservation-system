package com.airTecket.ticketCancellationService.service.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.airTecket.ticketCancellationService.dto.CancelResponseDTO;
import com.airTecket.ticketCancellationService.dto.CustomerAuthResponseDTO;
import com.airTecket.ticketCancellationService.dto.TicketDTO;
import com.airTecket.ticketCancellationService.exception.CustomerNotFoundException;
import com.airTecket.ticketCancellationService.exception.TicketNotFoundException;
import com.airTecket.ticketCancellationService.feign.BookingServiceClient;
import com.airTecket.ticketCancellationService.feign.FlightClient;
import com.airTecket.ticketCancellationService.mapper.CancellationMapper;
import com.airTecket.ticketCancellationService.model.Cancellation;
import com.airTecket.ticketCancellationService.repository.CancellationRepository;
import com.airTecket.ticketCancellationService.service.EmailService;
import com.airTecket.ticketCancellationService.service.TicketCancellationService;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TicketCancellationServiceImpl implements TicketCancellationService{

    @Autowired
    private BookingServiceClient bookingClient;
    @Autowired
    private FlightClient flightClient;
    
    @Autowired
    CancellationRepository cancellationRepository;
    

    @Autowired
    private EmailService emailService;
    @Transactional
    public CancelResponseDTO cancelTicket(String bookingId,LocalDate cancellationDate, CustomerAuthResponseDTO customerauthResponse) {
    	
    	
    	// 1. Fetch ticket
        TicketDTO ticket = bookingClient.getTicketByBookingId(bookingId);
        if (ticket == null) {
            throw new TicketNotFoundException("No booking found with ID: " + bookingId);
        }
        
        // 2. Extract the customerId 
        Integer customerId = customerauthResponse.getCustomerId();
        

        // 3. Validate ownership
        if (!ticket.getCustomerId().equals(customerId)&& !customerauthResponse.getRole().equals("ADMIN")) {
            throw new CustomerNotFoundException("Unauthorized: You do not own this ticket");
        }

        // 4. Check cancellation validity
        if (cancellationDate.isAfter(ticket.getJourneyDate())) {
            throw new RuntimeException("Cannot cancel after journey date");
        }

        // 5. Calculate cancellation charges & refund
        BigDecimal totalFare = ticket.getTotalFare();
        BigDecimal cancellationCharge = calculateCancellationCharge(ticket.getJourneyDate(), cancellationDate, totalFare);
        BigDecimal refundAmount = totalFare.subtract(cancellationCharge);
        
        Cancellation cancellation = Cancellation.builder()
        		.bookingId(bookingId)
                .cancellationDate(cancellationDate)
                .cancellationCharge(cancellationCharge)
                .refundAmount(refundAmount)
                .refundStatus("Pending")
                .customerId(customerId)
                .build();
        cancellationRepository.save(cancellation);

        // 6. Call FlightService to release seats
        if(!customerauthResponse.getRole().equals("ADMIN")) {
            flightClient.updateTicketsAndSeatAvailability(ticket.getFlightId(), ticket.getBookingId(), ticket.getSeatCount());
        }
        // 7. Update ticket status in BookingService
        bookingClient.updateTicketStatus(bookingId, "CANCELLED");
        
        // 8. Extract the email
        String email=customerauthResponse.getEmail();
        
        // 9.Sending a response mail
        emailService.sendResponseEmail(bookingId, email);

        // 10. Build response
        return CancellationMapper.toCancelResponse(ticket, cancellation);
    }

    // Helper to calculate charges
    private BigDecimal calculateCancellationCharge(LocalDate journeyDate, LocalDate cancellationDate, BigDecimal totalFare) {
        long daysBeforeJourney = ChronoUnit.DAYS.between(cancellationDate, journeyDate);
        BigDecimal chargePercentage;

        if (daysBeforeJourney > 10) {
            chargePercentage = new BigDecimal("0.10"); // 10% charge
        } else if (daysBeforeJourney > 5) {
            chargePercentage = new BigDecimal("0.20"); // 20% charge
        } else {
            chargePercentage = new BigDecimal("0.50"); // 50% charge
        }

        return totalFare.multiply(chargePercentage);
    }
}

