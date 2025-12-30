package com.airticket.searchservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    @Id
   // @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer flightId;
    
    @Column(nullable = false, length = 20,unique = true)
    @NotBlank
    @Size(max = 20)
    private String flightNumber;
    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String airlineName;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String fromAirport;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String toAirport;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime departureTime;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime arrivalTime;
    
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer totalSeats;
    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer availableSeats;
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal baseFare;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
//    @OneToMany(mappedBy = "flight")
//    private List<Ticket> tickets;
//
//    @OneToMany(mappedBy = "flight")
//    private List<Seat> seats;

}
