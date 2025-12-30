package com.airTecket.ticketCancellationService.service;

import java.time.LocalDate;

import com.airTecket.ticketCancellationService.dto.CancelResponseDTO;
import com.airTecket.ticketCancellationService.dto.CustomerAuthResponseDTO;


public interface TicketCancellationService {

	public CancelResponseDTO cancelTicket(String bookingId, LocalDate cancellationDate,CustomerAuthResponseDTO customerAuthResponse);
}
