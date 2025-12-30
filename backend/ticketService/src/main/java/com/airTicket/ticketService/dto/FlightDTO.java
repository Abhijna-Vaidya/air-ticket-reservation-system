package com.airTicket.ticketService.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDTO {

    private int flightId;
    @NotBlank
    @Size(max = 20)
    private String flightNumber;

    @NotBlank
    @Size(max = 100)
    private String airlineName;

    @NotBlank
    @Size(max = 100)
    private String fromAirport;

    @NotBlank
    @Size(max = 100)
    private String toAirport;

    @NotNull
    private LocalDateTime departureTime;

    @NotNull
    private LocalDateTime arrivalTime;

    @NotNull
    @Min(1)
    private Integer totalSeats;

    @NotNull
    @Min(0)
    private Integer availableSeats;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal baseFare;


}

