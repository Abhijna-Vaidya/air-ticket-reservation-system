package com.airTecket.ticketCancellationService.dto;


import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CancelResponseDTO {
	@NotBlank
    @Size(max = 20)
    private String bookingId;
	@NotNull
    @FutureOrPresent
    private LocalDate journeyDate;
	@NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal totalFare;
	@NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal cancellationCharge;
	@NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal refundAmount;
	@NotBlank
    private String refundStatus;
	@NotBlank
    private String message;
}

