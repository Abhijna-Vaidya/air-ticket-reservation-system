package com.airTecket.ticketCancellationService.mapper;

import com.airTecket.ticketCancellationService.dto.CancelResponseDTO;
import com.airTecket.ticketCancellationService.dto.TicketDTO;
import com.airTecket.ticketCancellationService.model.Cancellation;

public class CancellationMapper {

    public static CancelResponseDTO toCancelResponse(TicketDTO ticket, Cancellation cancellation) {
        return CancelResponseDTO.builder()
                .bookingId(ticket.getBookingId())
                .journeyDate(ticket.getJourneyDate())
                .totalFare(ticket.getTotalFare())
                .cancellationCharge(cancellation.getCancellationCharge())
                .refundAmount(cancellation.getRefundAmount())
                .refundStatus(cancellation.getRefundStatus())
                .message("Ticket cancelled. Refund will be processed within 3–5 days.")
                .build();
    }
}
