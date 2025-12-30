package com.airTicket.ticketService.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketBookingResponseDTO {
	private Integer ticketId;    
    private String bookingId;
    private Integer customerId;
    private Integer flightId;
    private String flightNumber;
    private String fromAirport;
    private String toAirport;
    private LocalDate journeyDate;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String bookingStatus;
    private LocalDateTime bookingDate;
    private Integer totalPassengers;
    private BigDecimal totalFare;
    private List<PassengerInfoDTO> passengers;
}
