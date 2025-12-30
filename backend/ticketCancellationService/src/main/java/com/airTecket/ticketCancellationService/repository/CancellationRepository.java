package com.airTecket.ticketCancellationService.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.airTecket.ticketCancellationService.model.Cancellation;

@Repository
public interface CancellationRepository extends JpaRepository<Cancellation, Integer> {
	Cancellation findByBookingId(String bookingId);
}
