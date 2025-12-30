package com.airTecket.ticketCancellationService.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
