package com.airticket.flight_service.service.impl;


import com.airticket.flight_service.exception.FlightAlreadyExistsException;
import com.airticket.flight_service.exception.FlightNotFoundException;
import com.airticket.flight_service.feign.TicketCancellationClient;
import com.airticket.flight_service.model.Flight;
import com.airticket.flight_service.repository.FlightRepository;
import com.airticket.flight_service.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

	@Autowired
	private FlightRepository flightRepo;


	@Autowired
	private TicketCancellationClient cancellationClient;

	@Override
	public Flight addFlight(Flight flight) throws FlightAlreadyExistsException {
        flight.setFlightId(null);
		return flightRepo.save(flight);
	}

	@Override
	public Flight updateFlight(int flightId, Flight flight) throws FlightNotFoundException {
		Flight existingFlight = flightRepo.findById(flightId).orElse(null);

		if (existingFlight == null || existingFlight.getFlightId() != flightId) {
			throw new FlightNotFoundException("Flight with the flight number "+flightId+" does not exist");
		}

		existingFlight.setFlightNumber(flight.getFlightNumber());
		existingFlight.setAirlineName(flight.getAirlineName());
		existingFlight.setFromAirport(flight.getFromAirport());
		existingFlight.setToAirport(flight.getToAirport());
		existingFlight.setDepartureTime(flight.getDepartureTime());
		existingFlight.setArrivalTime(flight.getArrivalTime());
		existingFlight.setTotalSeats(flight.getTotalSeats());
		existingFlight.setAvailableSeats(flight.getAvailableSeats());
		existingFlight.setBaseFare(flight.getBaseFare());

		existingFlight.setUpdatedAt(LocalDateTime.now());

		return flightRepo.save(existingFlight);
	}


	@Override
	@Transactional
	public String updateTicketsAndSeatAvailability(Integer flightId, String bookingId, Integer seats) throws FlightNotFoundException {
		Flight existingFlight = flightRepo.findById(flightId).orElse(null);

		if (existingFlight == null || existingFlight.getFlightId() != flightId) {
			throw new FlightNotFoundException("Flight with the flight number "+flightId+" does not exist");
		}
		int updateSeats = existingFlight.getAvailableSeats()+seats;
		existingFlight.setAvailableSeats(updateSeats);
		existingFlight.getBookingIds().remove(bookingId);
		flightRepo.save(existingFlight);

		return "success";
	}

	@Override
	@Transactional
	public Flight deleteFlight(int flightId , String token) throws FlightNotFoundException{
		Flight flight= flightRepo.findById(flightId).orElse(null);
		if(flight == null)
		{
			throw new FlightNotFoundException("Flight with the flight number "+flightId+" does not exist");
		}
		LocalDate today = LocalDate.now();
		for (String booking : flight.getBookingIds()) {

				cancellationClient.cancelTicket(token,booking,today);
			}

		flightRepo.delete(flight);
		return flight; 
	}

	@Override
	public Flight findFlightById(int flightId) throws FlightNotFoundException {
		Flight existingFlight = flightRepo.findById(flightId).orElse(null);

		if (existingFlight == null || existingFlight.getFlightId() != flightId) {
			throw new FlightNotFoundException("Flight with the flight number "+flightId+" does not exist");
		}
		return existingFlight;
	}

	@Override
	public List<Flight> findAllFlights() {
		return flightRepo.findAll();
	}

	@Override
	@Transactional
	public String addBookingIdAndUpdateSeats(Integer flightId, String bookingId, Integer noSeats) throws FlightNotFoundException {
		Flight existingFlight = flightRepo.findById(flightId).orElse(null);

		if (existingFlight == null || existingFlight.getFlightId() != flightId) {
			throw new FlightNotFoundException("Flight with the flight number "+flightId+" does not exist");
		}
		existingFlight.getBookingIds().add(bookingId);
		int updatedSeats = existingFlight.getAvailableSeats()-noSeats;
		existingFlight.setAvailableSeats(updatedSeats);
		flightRepo.save(existingFlight);
		return "success";
	}
}
