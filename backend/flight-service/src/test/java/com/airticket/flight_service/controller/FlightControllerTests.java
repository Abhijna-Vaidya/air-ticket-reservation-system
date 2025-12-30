package com.airticket.flight_service.controller;

import com.airticket.flight_service.dto.FlightDTO;
import com.airticket.flight_service.exception.FlightAlreadyExistsException;
import com.airticket.flight_service.exception.FlightNotFoundException;
import com.airticket.flight_service.mapper.FlightMapper;
import com.airticket.flight_service.model.Flight;
import com.airticket.flight_service.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightControllerUnitTest {

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightController flightController;

    private Flight sampleFlight;
    private FlightDTO sampleDto;

    @BeforeEach
    void setUp() {
        sampleFlight = Flight.builder()
                .flightId(1)
                .flightNumber("AI100")
                .airlineName("Air India")
                .fromAirport("DEL")
                .toAirport("BOM")
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .totalSeats(180)
                .availableSeats(180)
                .baseFare(new BigDecimal("5000.00"))
                .build();

        sampleDto = FlightMapper.toDTO(sampleFlight);
    }

    @Test
    void addFlight_shouldReturnCreated_whenServiceSucceeds() throws FlightAlreadyExistsException {
        when(flightService.addFlight(any(Flight.class))).thenReturn(sampleFlight);

        ResponseEntity<FlightDTO> response = flightController.addFlight(sampleDto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(sampleFlight.getFlightId(), response.getBody().getFlightId());
        verify(flightService).addFlight(any(Flight.class));
    }

    @Test
    void addFlight_shouldPropagate_whenAlreadyExists() throws FlightAlreadyExistsException {
        when(flightService.addFlight(any(Flight.class)))
                .thenThrow(new FlightAlreadyExistsException("already exists"));

        assertThrows(FlightAlreadyExistsException.class, () -> flightController.addFlight(sampleDto));
        verify(flightService).addFlight(any(Flight.class));
    }

    @Test
    void getFlightById_shouldReturnFlight_whenFound() throws FlightNotFoundException {
        when(flightService.findFlightById(1)).thenReturn(sampleFlight);

        ResponseEntity<FlightDTO> response = flightController.getFlightById(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sampleFlight.getFlightId(), response.getBody().getFlightId());
        verify(flightService).findFlightById(1);
    }

    @Test
    void getFlightById_shouldThrowNotFound_whenMissing() throws FlightNotFoundException {
        when(flightService.findFlightById(42)).thenThrow(new FlightNotFoundException("not found"));

        assertThrows(FlightNotFoundException.class, () -> flightController.getFlightById(42));
        verify(flightService).findFlightById(42);
    }

    @Test
    void getAllFlights_shouldReturnList() {
        when(flightService.findAllFlights()).thenReturn(List.of(sampleFlight));

        ResponseEntity<List<FlightDTO>> response = flightController.getAllFlights();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(flightService).findAllFlights();
    }

    @Test
    void updateFlight_shouldReturnUpdated_whenOk() throws FlightNotFoundException {
        Flight updated = Flight.builder()
                .flightId(5)
                .flightNumber("AI101")
                .airlineName("Air India")
                .fromAirport("DEL")
                .toAirport("BOM")
                .departureTime(sampleFlight.getDepartureTime())
                .arrivalTime(sampleFlight.getArrivalTime())
                .totalSeats(180)
                .availableSeats(180)
                .baseFare(new BigDecimal("5200.00"))
                .build();

        FlightDTO updatedDto = FlightMapper.toDTO(updated);
        // service expects id param and entity, returns updated flight
        when(flightService.updateFlight(eq(5), any(Flight.class))).thenReturn(updated);

        ResponseEntity<FlightDTO> response = flightController.updateFlight(5, updatedDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(5, response.getBody().getFlightId());
        assertEquals("AI101", response.getBody().getFlightNumber());
        verify(flightService).updateFlight(eq(5), any(Flight.class));
    }

    @Test
    void updateFlight_shouldThrowNotFound_whenMissing() throws FlightNotFoundException {
        FlightDTO dto = FlightMapper.toDTO(sampleFlight);
        when(flightService.updateFlight(eq(99), any(Flight.class))).thenThrow(new FlightNotFoundException("not found"));

        assertThrows(FlightNotFoundException.class, () -> flightController.updateFlight(99, dto));
        verify(flightService).updateFlight(eq(99), any(Flight.class));
    }

    @Test
    void deleteFlight_shouldReturnDeleted_whenOk() throws FlightNotFoundException {
        // Controller expects Authorization header but only passes token to service; since we're calling
        // controller method directly, just pass a string token.
        String token = "Bearer token";
        when(flightService.deleteFlight(1, token)).thenReturn(sampleFlight);

        ResponseEntity<FlightDTO> response = flightController.deleteFlight(1, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sampleFlight.getFlightId(), response.getBody().getFlightId());
        verify(flightService).deleteFlight(1, token);
    }

    @Test
    void deleteFlight_shouldThrowNotFound_whenMissing() throws FlightNotFoundException {
        String token = "Bearer token";
        when(flightService.deleteFlight(50, token)).thenThrow(new FlightNotFoundException("not found"));

        assertThrows(FlightNotFoundException.class, () -> flightController.deleteFlight(50, token));
        verify(flightService).deleteFlight(50, token);
    }

    @Test
    void updateTicketsAndSeatAvailability_shouldReturnSuccess() throws FlightNotFoundException {
        when(flightService.updateTicketsAndSeatAvailability(1, "B001", 2)).thenReturn("success");

        ResponseEntity<String> response = flightController.updateTicketsAndSeatAvailability(1, "B001", 2);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody());
        verify(flightService).updateTicketsAndSeatAvailability(1, "B001", 2);
    }

    @Test
    void addBookingIdAndUpdateSeats_shouldReturnSuccess() throws FlightNotFoundException {
        when(flightService.addBookingIdAndUpdateSeats(1, "B001", 2)).thenReturn("success");

        ResponseEntity<String> response = flightController.addBookingIdAndUpdateSeats(1, "B001", 2);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody());
        verify(flightService).addBookingIdAndUpdateSeats(1, "B001", 2);
    }
}
