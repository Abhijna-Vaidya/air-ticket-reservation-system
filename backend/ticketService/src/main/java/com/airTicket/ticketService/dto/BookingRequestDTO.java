package com.airTicket.ticketService.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequestDTO {
    private Integer customerId;
    private Integer flightId;
    private LocalDate journeyDate;
    private String seatClass;
    private List<PassengerInfoDTO> passengers;
    private String paymentMode;
}
