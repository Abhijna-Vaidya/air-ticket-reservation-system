package com.airticket.flight_service.repository;


import com.airticket.flight_service.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FlightRepository extends JpaRepository<Flight, Integer> {

}