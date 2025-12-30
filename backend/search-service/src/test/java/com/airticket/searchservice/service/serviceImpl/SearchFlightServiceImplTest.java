package com.airticket.searchservice.service.serviceImpl;

import com.airticket.searchservice.dto.FlightSearchResponseDTO;
import com.airticket.searchservice.model.Flight;
import com.airticket.searchservice.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SearchFlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private SearchFlightServiceImpl searchFlightService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchFlight() {
        String from = "Delhi";
        String to = "Mumbai";
        LocalDate date = LocalDate.of(2025, 7, 30);

        Flight flight = new Flight();
        flight.setFromAirport(from);
        flight.setToAirport(to);
        flight.setAirlineName("Vistara");

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        Page<Flight> mockPage = new PageImpl<>(Collections.singletonList(flight), PageRequest.of(0, 5), 1);

        when(flightRepository.findByRouteAndDateRange(from, to, start, end, PageRequest.of(0, 5)))
                .thenReturn(mockPage);

        List<FlightSearchResponseDTO> result = searchFlightService.searchFlights(from, to, date, 0, 5);

        assertEquals(1, result.size());
        assertEquals("Vistara", result.get(0).getAirlineName());

        verify(flightRepository, times(1)).findByRouteAndDateRange(from, to, start, end, PageRequest.of(0, 5));
    }
}
