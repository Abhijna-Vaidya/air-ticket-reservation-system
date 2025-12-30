package com.airticket.searchservice.controller;

import com.airticket.searchservice.dto.FlightSearchResponseDTO;
import com.airticket.searchservice.service.SearchFlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchFlightControllerTest {

    @Mock
    private SearchFlightService searchFlightService;

    @InjectMocks
    private SearchFlightController controller;

    private List<FlightSearchResponseDTO> mockFlights;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        FlightSearchResponseDTO dto1 = new FlightSearchResponseDTO();
        dto1.setFlightNumber("AI123");
        dto1.setAirlineName("Air India");

        FlightSearchResponseDTO dto2 = new FlightSearchResponseDTO();
        dto2.setFlightNumber("6E456");
        dto2.setAirlineName("IndiGo");

        mockFlights = Arrays.asList(dto1, dto2);
    }

    @Test
    void testSearchFlight_success() {
        String from = "Delhi";
        String to = "Mumbai";
        LocalDate date = LocalDate.of(2025, 8, 1);
        int page = 0;
        int size = 5;

        when(searchFlightService.searchFlights(from, to, date, page, size)).thenReturn(mockFlights);

        ResponseEntity<List<FlightSearchResponseDTO>> response = controller.searchFlights(from, to, date, page, size);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("AI123", response.getBody().get(0).getFlightNumber());
    }

    @Test
    void testSearchFlight_noResults() {
        int page = 0;
        int size = 5;

        when(searchFlightService.searchFlights("Hyd", "Chennai", LocalDate.now(), page, size)).thenReturn(List.of());

        ResponseEntity<List<FlightSearchResponseDTO>> response =
                controller.searchFlights("Hyd", "Chennai", LocalDate.now(), page, size);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testSearchWithFilters_success() {
        String from = "Bengaluru";
        String to = "Kochi";
        LocalDate date = LocalDate.of(2025, 8, 1);
        String airline = "Air India";
        BigDecimal minFare = new BigDecimal("1000.00");
        BigDecimal maxFare = new BigDecimal("8000.00");
        Integer minSeats = 2;
        int page = 0;
        int size = 5;

        when(searchFlightService.searchWithFilters(from, to, date, airline, minFare, maxFare, minSeats, page, size))
                .thenReturn(mockFlights);

        ResponseEntity<List<FlightSearchResponseDTO>> response = controller.searchWithFilters(
                from, to, date, airline, minFare, maxFare, minSeats, page, size);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Air India", response.getBody().get(0).getAirlineName());
    }

    @Test
    void testSearchWithFilters_emptyResult() {
        String from = "Pune";
        String to = "Nagpur";
        LocalDate date = LocalDate.of(2025, 8, 1);
        int page = 0;
        int size = 5;

        when(searchFlightService.searchWithFilters(eq(from), eq(to), eq(date), any(), any(), any(), any(), eq(page), eq(size)))
                .thenReturn(List.of());

        ResponseEntity<List<FlightSearchResponseDTO>> response = controller.searchWithFilters(
                from, to, date, null, null, null, null, page, size);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}
