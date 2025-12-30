package com.airticket.flight_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAuthResponseDTO {
	@NotNull
    private boolean valid;

    @NotNull
    private Integer customerId;
    
    @NotNull
    private String email;

    @NotBlank
    private String role;
}
