package com.airticket.flight_service.service.impl;

import com.airticket.flight_service.exception.FlightAlreadyExistsException;
import com.airticket.flight_service.exception.FlightNotFoundException;
import com.airticket.flight_service.feign.TicketCancellationClient;
import com.airticket.flight_service.model.Flight;
import com.airticket.flight_service.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepo;

    @Mock
    private TicketCancellationClient cancellationClient;

    @InjectMocks
    private FlightServiceImpl flightService;

    private Flight sample;

    @BeforeEach
    void setUp() {
        sample = Flight.builder()
                .flightId(1)
                .flightNumber("AI100")
                .airlineName("Air India")
                .fromAirport("DEL")
                .toAirport("BOM")
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .totalSeats(180)
                .availableSeats(100)
                .baseFare(new BigDecimal("4500.00"))
                .bookingIds(new ArrayList<>())
                .build();
    }

    // ----------------- addFlight -----------------
    @Test
    void addFlight_shouldNullifyIdAndSave() throws FlightAlreadyExistsException {
        Flight input = Flight.builder()
                .flightId(999)
                .flightNumber("X1")
                .airlineName("Test")
                .fromAirport("A")
                .toAirport("B")
                .departureTime(LocalDateTime.now())
                .arrivalTime(LocalDateTime.now().plusHours(2))
                .totalSeats(10)
                .availableSeats(10)
                .baseFare(new BigDecimal("100"))
                .build();

        // verify service nullifies id before calling save
        when(flightRepo.save(any(Flight.class))).thenAnswer(invocation -> {
            Flight arg = invocation.getArgument(0);
            // assert that service cleared the id before calling save
            assertThat(arg.getFlightId()).isNull();
            // simulate DB assigning id
            arg.setFlightId(42);
            return arg;
        });

        Flight saved = flightService.addFlight(input);

        assertThat(saved).isNotNull();
        assertThat(saved.getFlightId()).isEqualTo(42);
        verify(flightRepo).save(any(Flight.class));
    }

    // ----------------- updateFlight -----------------
    @Test
    void updateFlight_shouldUpdateFieldsAndSetUpdatedAt() throws FlightNotFoundException {
        when(flightRepo.findById(1)).thenReturn(Optional.of(sample));
        when(flightRepo.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

        Flight update = Flight.builder()
                .flightNumber("AI101")
                .airlineName("Air India Updated")
                .fromAirport("DEL")
                .toAirport("HYD")
                .departureTime(LocalDateTime.now().plusDays(2))
                .arrivalTime(LocalDateTime.now().plusDays(2).plusHours(2))
                .totalSeats(200)
                .availableSeats(180)
                .baseFare(new BigDecimal("5000"))
                .build();

        Flight result = flightService.updateFlight(1, update);

        assertThat(result.getFlightNumber()).isEqualTo("AI101");
        assertThat(result.getAirlineName()).isEqualTo("Air India Updated");
        assertThat(result.getAvailableSeats()).isEqualTo(180);
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(flightRepo).save(result);
    }

    @Test
    void updateFlight_whenNotFound_shouldThrowFlightNotFoundException() {
        when(flightRepo.findById(999)).thenReturn(Optional.empty());
        Flight upd = new Flight();
        assertThatThrownBy(() -> flightService.updateFlight(999, upd))
                .isInstanceOf(FlightNotFoundException.class);
    }

    @Test
    void updateFlight_whenIdMismatch_shouldThrowFlightNotFoundException() {
        // existing flight has id 1, try to update with flightId mismatch scenario
        when(flightRepo.findById(anyInt())).thenReturn(Optional.of(sample));

        Flight upd = Flight.builder().flightId(2).build(); // mismatch value not used in check (service compares existing id to arg flightId)
        // The service checks `existingFlight.getFlightId() != flightId` where flightId param is passed separately
        assertThatThrownBy(() -> flightService.updateFlight(2, upd))
                .isInstanceOf(FlightNotFoundException.class);
    }

    // ----------------- updateTicketsAndSeatAvailability -----------------
    @Test
    void updateTicketsAndSeatAvailability_shouldIncreaseSeatsAndRemoveBooking() throws FlightNotFoundException {
        sample.getBookingIds().add("B001");
        when(flightRepo.findById(1)).thenReturn(Optional.of(sample));
        when(flightRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String res = flightService.updateTicketsAndSeatAvailability(1, "B001", 2);

        assertThat(res).isEqualTo("success");
        assertThat(sample.getAvailableSeats()).isEqualTo(102); // 100 + 2
        assertThat(sample.getBookingIds()).doesNotContain("B001");
        verify(flightRepo).save(sample);
    }

    @Test
    void updateTicketsAndSeatAvailability_bookingNotPresent_shouldStillIncreaseSeats() throws FlightNotFoundException {
        // booking list empty
        when(flightRepo.findById(1)).thenReturn(Optional.of(sample));
        when(flightRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        flightService.updateTicketsAndSeatAvailability(1, "NON_EXIST", 5);

        assertThat(sample.getAvailableSeats()).isEqualTo(105); // 100 + 5
        verify(flightRepo).save(sample);
    }

    @Test
    void updateTicketsAndSeatAvailability_whenNotFound_shouldThrow() {
        when(flightRepo.findById(2)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> flightService.updateTicketsAndSeatAvailability(2, "B1", 1))
                .isInstanceOf(FlightNotFoundException.class);
    }

    // ----------------- deleteFlight -----------------
    @Test
    void deleteFlight_shouldCallCancellationForEachBookingAndDelete() throws FlightNotFoundException {
        sample.getBookingIds().addAll(List.of("BK1", "BK2"));
        when(flightRepo.findById(1)).thenReturn(Optional.of(sample));

        Flight result = flightService.deleteFlight(1, "Bearer token");

        assertThat(result).isEqualTo(sample);
        // verify cancelTicket called twice
        verify(cancellationClient, times(2)).cancelTicket(eq("Bearer token"), anyString(), any(LocalDate.class));
        verify(flightRepo).delete(sample);
    }

    @Test
    void deleteFlight_whenNoBookings_shouldDeleteWithoutCallingCancellationClient() throws FlightNotFoundException {
        // bookingIds empty
        when(flightRepo.findById(1)).thenReturn(Optional.of(sample));

        flightService.deleteFlight(1, "tok");

        verify(cancellationClient, times(0)).cancelTicket(anyString(), anyString(), any());
        verify(flightRepo).delete(sample);
    }

    @Test
    void deleteFlight_whenNotFound_shouldThrow() {
        when(flightRepo.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> flightService.deleteFlight(99, "tok"))
                .isInstanceOf(FlightNotFoundException.class);
    }

    // ----------------- findFlightById -----------------
    @Test
    void findFlightById_shouldReturnFlight() throws FlightNotFoundException {
        when(flightRepo.findById(1)).thenReturn(Optional.of(sample));
        Flight f = flightService.findFlightById(1);
        assertThat(f).isSameAs(sample);
    }

    @Test
    void findFlightById_whenNotFound_shouldThrow() {
        when(flightRepo.findById(123)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> flightService.findFlightById(123))
                .isInstanceOf(FlightNotFoundException.class);
    }

    // ----------------- findAllFlights -----------------
    @Test
    void findAllFlights_shouldReturnList() {
        when(flightRepo.findAll()).thenReturn(List.of(sample));
        List<Flight> list = flightService.findAllFlights();
        assertThat(list).hasSize(1).containsExactly(sample);
    }

    // ----------------- addBookingIdAndUpdateSeats -----------------
    @Test
    void addBookingIdAndUpdateSeats_shouldAddBookingAndDecreaseAvailableSeats() throws FlightNotFoundException {
        when(flightRepo.findById(1)).thenReturn(Optional.of(sample));
        when(flightRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String res = flightService.addBookingIdAndUpdateSeats(1, "BK_NEW", 3);

        assertThat(res).isEqualTo("success");
        assertThat(sample.getBookingIds()).contains("BK_NEW");
        assertThat(sample.getAvailableSeats()).isEqualTo(97); // 100 - 3
        verify(flightRepo).save(sample);
    }

    @Test
    void addBookingIdAndUpdateSeats_whenNotFound_shouldThrow() {
        when(flightRepo.findById(77)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> flightService.addBookingIdAndUpdateSeats(77, "b", 2))
                .isInstanceOf(FlightNotFoundException.class);
    }

    // ----------------- defensive/null-behavior test (document current behavior) -----------------
    @Test
    void deleteFlight_whenBookingIdsNull_shouldThrowNullPointer() {
        Flight f = Flight.builder()
                .flightId(5)
                .bookingIds(null) // simulate bad entity state
                .build();

        when(flightRepo.findById(5)).thenReturn(Optional.of(f));

        // current implementation iterates bookingIds -> will throw NPE
        assertThatThrownBy(() -> flightService.deleteFlight(5, "tok"))
                .isInstanceOf(NullPointerException.class);
    }
}
