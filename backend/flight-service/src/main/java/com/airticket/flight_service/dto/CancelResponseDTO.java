package com.airticket.flight_service.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

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

