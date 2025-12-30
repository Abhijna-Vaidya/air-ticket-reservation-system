package com.airticket.searchservice.service.serviceImpl;

import com.airticket.searchservice.dto.FlightSearchResponseDTO;
import com.airticket.searchservice.exception.FlightSearchException;
import com.airticket.searchservice.mapper.FlightSearchMapper;
import com.airticket.searchservice.model.Flight;
import com.airticket.searchservice.repository.FlightRepository;
import com.airticket.searchservice.service.SearchFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchFlightServiceImpl implements SearchFlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Override
    public List<FlightSearchResponseDTO> searchFlights(String fromAirport, String toAirport, LocalDate departureDate, int page, int size) {

        if (fromAirport != null && fromAirport.equalsIgnoreCase(toAirport)) {
            throw new FlightSearchException("Departure and destination airports cannot be the same.");
        }
        LocalDateTime start = departureDate.atStartOfDay();
        LocalDateTime end = departureDate.plusDays(1).atStartOfDay();

        Pageable pageable = PageRequest.of(page, size);

        Page<Flight> flightsPage = flightRepository.findByRouteAndDateRange(fromAirport, toAirport, start, end, pageable);

        if (flightsPage.isEmpty()) {
            throw new FlightSearchException("No flights found from " + fromAirport + " to " + toAirport + " on " + departureDate);
        }

        List<FlightSearchResponseDTO> responseList = new ArrayList<>();
        for (Flight flight : flightsPage.getContent()) {
            FlightSearchResponseDTO dto = FlightSearchMapper.toDTO(flight);
            responseList.add(dto);
        }
        return responseList;
    }

    @Override
    public List<FlightSearchResponseDTO> searchWithFilters(String fromAirport, String toAirport, LocalDate date,
                                                           String airlineName, BigDecimal minFare,
                                                           BigDecimal maxFare, Integer minSeats,
                                                           int page, int size) {

        if (fromAirport != null && fromAirport.equalsIgnoreCase(toAirport)) {
            throw new FlightSearchException("Departure and destination airports cannot be the same.");
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        Pageable pageable = PageRequest.of(page, size);

        Page<Flight> flightsPage = flightRepository.findWithOptionalFilters(
                fromAirport, toAirport, airlineName, minFare, maxFare, minSeats, start, end, pageable
        );

        if (flightsPage.isEmpty()) {
            throw new FlightSearchException("No flights found with the given filters on " + date);
        }

        List<FlightSearchResponseDTO> responseList = new ArrayList<>();
        for (Flight flight : flightsPage.getContent()) {
            FlightSearchResponseDTO dto = FlightSearchMapper.toDTO(flight);
            responseList.add(dto);
        }
        return responseList;
    }
}
