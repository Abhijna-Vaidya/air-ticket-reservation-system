package com.airticket.searchservice.controller;

import com.airticket.searchservice.dto.FlightSearchResponseDTO;
import com.airticket.searchservice.service.SearchFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/filter/flights")
public class SearchFlightController {

    @Autowired
    private SearchFlightService searchFlightService;

    @GetMapping("/search")
    public ResponseEntity<List<FlightSearchResponseDTO>> searchFlights(
            @RequestParam String fromAirport,
            @RequestParam String toAirport,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        List<FlightSearchResponseDTO> flights = searchFlightService
                .searchFlights(fromAirport, toAirport, departureDate, page, size);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<List<FlightSearchResponseDTO>> searchWithFilters(
            @RequestParam String fromAirport,
            @RequestParam String toAirport,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam(required = false) String airlineName,
            @RequestParam(required = false) BigDecimal minFare,
            @RequestParam(required = false) BigDecimal maxFare,
            @RequestParam(required = false) Integer minSeats,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        List<FlightSearchResponseDTO> flights = searchFlightService.searchWithFilters(
                fromAirport, toAirport, departureDate,
                airlineName, minFare, maxFare, minSeats, page, size);

        return ResponseEntity.ok(flights);
    }
}
