package com.airticket.flight_service.controller;


import com.airticket.flight_service.dto.FlightDTO;
import com.airticket.flight_service.exception.FlightAlreadyExistsException;
import com.airticket.flight_service.exception.FlightNotFoundException;
import com.airticket.flight_service.mapper.FlightMapper;
import com.airticket.flight_service.model.Flight;
import com.airticket.flight_service.service.FlightService;
import jakarta.validation.Valid;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
//@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/flights")
public class FlightController {


    @Autowired
    private FlightService flightService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlightDTO> addFlight(@Valid @RequestBody FlightDTO flightDTO) throws FlightAlreadyExistsException {
        Flight flight = FlightMapper.toEntity(flightDTO);
        Flight saved = flightService.addFlight(flight);
        return new ResponseEntity<>(FlightMapper.toDTO(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDTO> getFlightById(@PathVariable("id") int flightId) throws FlightNotFoundException {
        Flight fetchedFlight =flightService.findFlightById(flightId);
        FlightDTO flightDto = FlightMapper.toDTO(fetchedFlight);
        return new ResponseEntity<>(flightDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        List<Flight> allFlights =flightService.findAllFlights();
        List<FlightDTO> flightsDtos = allFlights.stream().map(FlightMapper::toDTO).toList();
        return new ResponseEntity<>(flightsDtos, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlightDTO> updateFlight(@PathVariable("id") int id,
                                                  @Valid @RequestBody FlightDTO flightDTO) throws FlightNotFoundException {
        flightDTO.setFlightId(id);
        Flight updated = flightService.updateFlight(id, FlightMapper.toEntity(flightDTO));
        return  new ResponseEntity<>(FlightMapper.toDTO(updated),HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlightDTO> deleteFlight(@PathVariable("id") int id , @RequestHeader("Authorization") String token) throws FlightNotFoundException {
        Flight deleted = flightService.deleteFlight(id , token);
        return ResponseEntity.ok(FlightMapper.toDTO(deleted));
    }

    @PutMapping("{flightId}/seats")
    public ResponseEntity<String> updateTicketsAndSeatAvailability(@PathVariable("flightId") Integer flightId, @RequestParam("bookingId") String bookingId, @RequestParam("seats") Integer seats)
            throws FlightNotFoundException {
        String status = flightService.updateTicketsAndSeatAvailability(flightId,bookingId,seats);
        return ResponseEntity.ok(status);
    }

    @PutMapping("addBookingId/{flightId}/{noSeats}")
    public ResponseEntity<String> addBookingIdAndUpdateSeats(@PathVariable("flightId") Integer flightId, @RequestParam("bookingId") String bookingId,@RequestParam("noSeats") Integer noSeats)
            throws FlightNotFoundException {
        String status = flightService.addBookingIdAndUpdateSeats(flightId,bookingId,noSeats);
        return ResponseEntity.ok(status);
    }

}
