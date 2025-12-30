package com.airTicket.ticketService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CustomerAuthResponseDTO {
    @NotNull
    private boolean valid;

    @NotNull
    private Integer customerId;


    @NotBlank
    private String email;

    @NotBlank
    private String role;
}
