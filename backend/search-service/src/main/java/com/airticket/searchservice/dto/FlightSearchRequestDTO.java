package com.airticket.searchservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchRequestDTO {
    @NotBlank(message = "From airport is required")
    private String fromAirport;

    @NotBlank(message = "To airport is required")
    private String toAirport;

    @NotNull(message = "Departure date is required")
    private LocalDate departureDate;

    private String airlineName;

    @DecimalMin(value = "0.0", message = "Minimum fare must be positive")
    private BigDecimal minFare;

    @DecimalMin(value = "0.0", message = "Maximum fare must be positive")
    private BigDecimal maxFare;

    @Min(value = 0, message = "Minimum seats must be non-negative")
    private Integer minSeats;
}