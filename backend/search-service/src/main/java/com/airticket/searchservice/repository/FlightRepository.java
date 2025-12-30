package com.airticket.searchservice.repository;

import com.airticket.searchservice.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Integer> {

    @Query("SELECT f FROM Flight f " +
            "WHERE f.fromAirport = :from " +
            "AND f.toAirport = :to " +
            "AND f.departureTime BETWEEN :start AND :end")
    Page<Flight> findByRouteAndDateRange(
            @Param("from") String from,
            @Param("to") String to,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    @Query("SELECT f FROM Flight f WHERE " +
            "(:from IS NULL OR f.fromAirport = :from) AND " +
            "(:to IS NULL OR f.toAirport = :to) AND " +
            "(:airlineName IS NULL OR f.airlineName = :airlineName) AND " +
            "(:minFare IS NULL OR f.baseFare >= :minFare) AND " +
            "(:maxFare IS NULL OR f.baseFare <= :maxFare) AND " +
            "(:minSeats IS NULL OR f.availableSeats >= :minSeats) AND " +
            "f.departureTime BETWEEN :start AND :end")
    Page<Flight> findWithOptionalFilters(
            @Param("from") String from,
            @Param("to") String to,
            @Param("airlineName") String airlineName,
            @Param("minFare") BigDecimal minFare,
            @Param("maxFare") BigDecimal maxFare,
            @Param("minSeats") Integer minSeats,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );
}