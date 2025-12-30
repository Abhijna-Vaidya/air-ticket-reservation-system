package com.airticket.flight_service.service;



import com.airticket.flight_service.exception.FlightAlreadyExistsException;
import com.airticket.flight_service.exception.FlightNotFoundException;
import com.airticket.flight_service.model.Flight;

import java.util.List;

public interface FlightService {

	public Flight addFlight(Flight flight) throws FlightAlreadyExistsException;
	public Flight updateFlight(int flightId,Flight flight)throws FlightNotFoundException;
	public Flight deleteFlight(int flightId ,String token) throws FlightNotFoundException;

	public List<Flight> findAllFlights();

	Flight findFlightById(int flightId)throws FlightNotFoundException;

	String updateTicketsAndSeatAvailability(Integer flightId, String bookinId, Integer seats) throws FlightNotFoundException;

	String addBookingIdAndUpdateSeats(Integer flightId, String bookinId, Integer Noseats) throws FlightNotFoundException;
}
