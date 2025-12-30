package com.airticket.searchservice.service;

import com.airticket.searchservice.dto.FlightSearchResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SearchFlightService {
    List<FlightSearchResponseDTO> searchFlights(String from, String to, LocalDate date, int page, int size);

    List<FlightSearchResponseDTO> searchWithFilters(
            String fromAirport,
            String toAirport,
            LocalDate departureDate,
            String airlineName,
            BigDecimal minFare,
            BigDecimal maxFare,
            Integer minSeats,
            int page,
            int size
    );
}

