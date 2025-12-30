package com.airticket.searchservice.mapper;

import com.airticket.searchservice.dto.FlightSearchResponseDTO;
import com.airticket.searchservice.model.Flight;

public class FlightSearchMapper {

    public static FlightSearchResponseDTO toDTO(Flight flight) {
        return FlightSearchResponseDTO.builder()
                .flightId(flight.getFlightId())
                .flightNumber(flight.getFlightNumber())
                .airlineName(flight.getAirlineName())
                .fromAirport(flight.getFromAirport())
                .toAirport(flight.getToAirport())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .totalSeats(flight.getTotalSeats())
                .availableSeats(flight.getAvailableSeats())
                .baseFare(flight.getBaseFare())
                .build();
    }
}