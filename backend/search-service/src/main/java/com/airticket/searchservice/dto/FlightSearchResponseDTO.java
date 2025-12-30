package com.airticket.searchservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchResponseDTO {
    private int flightId;
    private String flightNumber;
    private String airlineName;
    private String fromAirport;
    private String toAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal baseFare;
}