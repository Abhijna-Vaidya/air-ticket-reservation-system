package com.airticket.flight_service.mapper;



import com.airticket.flight_service.dto.FlightDTO;
import com.airticket.flight_service.model.Flight;

import java.time.LocalDateTime;

public class FlightMapper {

    public static FlightDTO toDTO(Flight flight) {
        return FlightDTO.builder()
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

    public static Flight toEntity(FlightDTO dto) {
        return Flight.builder()
                .flightId(dto.getFlightId())
                .flightNumber(dto.getFlightNumber())
                .airlineName(dto.getAirlineName())
                .fromAirport(dto.getFromAirport())
                .toAirport(dto.getToAirport())
                .departureTime(dto.getDepartureTime())
                .arrivalTime(dto.getArrivalTime())
                .totalSeats(dto.getTotalSeats())
                .availableSeats(dto.getAvailableSeats())
                .baseFare(dto.getBaseFare())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
