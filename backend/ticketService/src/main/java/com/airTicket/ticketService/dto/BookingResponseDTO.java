package com.airTicket.ticketService.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class BookingResponseDTO {
    private Integer ticketId;
    private String bookingId;
    private BigDecimal totalFare;
    private String status;
    private LocalDate journeyDate;
    private List<String> seatNumbers;
    private String paymentStatus;
}
