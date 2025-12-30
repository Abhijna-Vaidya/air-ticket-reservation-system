package com.airTicket.ticketService.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class TicketDTO {
	
	@NotBlank
	private Integer ticketId;
	
	@NotBlank
    @Size(max = 50)
    private String bookingId;

    @NotNull
    private Integer customerId;

    @NotNull
    private Integer flightId;

    @NotNull
    private LocalDate journeyDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalFare;

    @NotNull
    @Min(value = 1)
    private Integer seatCount;

    @NotBlank
    private String status;
}
